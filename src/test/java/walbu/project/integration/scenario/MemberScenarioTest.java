package walbu.project.integration.scenario;

import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.JsonFieldType;

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

public class MemberScenarioTest extends IntegrationTest {


    @Autowired
    private MemberRepository memberRepository;

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
                .post("/api/members/sign-up")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo(exception.getMessage()));
    }

    @Test
    @DisplayName("잘못된 암호로 로그인을 시도하면 실패한다.")
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
                .post("/api/members/sign-up")
                .then()
                .extract()
                .response();

        LoginRequest loginRequest = new LoginRequest("nag", "1234!");

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/members/login")
                .then().log().all()
                .statusCode(401);
    }

}
