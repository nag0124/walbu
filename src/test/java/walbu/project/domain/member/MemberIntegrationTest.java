package walbu.project.domain.member;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import walbu.project.IntegrationTest;
import walbu.project.common.error.exception.SameNameMemberExistsException;
import walbu.project.domain.member.data.Member;
import walbu.project.domain.member.data.MemberType;
import walbu.project.domain.member.data.dto.CreateMemberRequest;
import walbu.project.domain.member.data.dto.LoginRequest;
import walbu.project.domain.member.repository.MemberRepository;

public class MemberIntegrationTest extends IntegrationTest {


    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 가입한다.")
    void signUp() {
        // given
        CreateMemberRequest request = new CreateMemberRequest(
                "nag",
                "nag@walbu.com",
                "1q2w3e4r!",
                "01012341234",
                MemberType.STUDENT
        );

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/members")
                .then().log().all()
                .statusCode(200)
                .body("memberId", notNullValue());

    }

    @Test
    @DisplayName("동일한 이름의 회원이 가입하려고 하면 에러가 발생한다.")
    void sameNameCantSinUp() {
        // given
        Member member = new Member(
                "nag",
                "nag@walbu.com",
                "1q2w3e4r!",
                "01012341234",
                MemberType.STUDENT
        ) ;
        memberRepository.save(member);

        CreateMemberRequest request = new CreateMemberRequest(
                member.getName(),
                "asdf@walbu.com",
                "1q2w3e4r!",
                "01012341234",
                MemberType.STUDENT
        );
        SameNameMemberExistsException exception = new SameNameMemberExistsException();

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/members")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo(exception.getMessage()));
    }

    @Test
    @DisplayName("로그인 한다.")
    void login() {
        // given
        CreateMemberRequest createMemberRequest = new CreateMemberRequest(
                "nag",
                "nag@walbu.com",
                "1q2w3e4r!",
                "01012341234",
                MemberType.STUDENT
        );

        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(createMemberRequest)
                .when()
                .post("/api/members")
                .then()
                .extract()
                .response();

        int memberId = response.jsonPath().getInt("memberId");

        LoginRequest loginRequest = new LoginRequest("name", "password");

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(createMemberRequest)
                .when()
                .post("/api/members/login")
                .then().log().all()
                .statusCode(200)
                .body("memberId", equalTo(memberId));
    }



}
