package walbu.project.integration.documentation;

import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.JsonFieldType;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import walbu.project.IntegrationTest;
import walbu.project.domain.member.data.MemberType;
import walbu.project.domain.member.data.dto.CreateMemberRequest;
import walbu.project.domain.member.data.dto.LoginRequest;
import walbu.project.domain.member.repository.MemberRepository;

public class MemberDocumentationTest extends IntegrationTest {

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
                .given(this.spec).log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
                .filter(document("{class-name}/{method-name}",
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("멤버 이름")
                                        .attributes(key("format").value("중복 불가")),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("멤버 이메일")
                                        .attributes(key("format").value("이메일 양식 ex)nag@walbu.com")),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("멤버 암호")
                                        .attributes(key("format").value("6자 이상 8자 이하 " +
                                                "+\n영문 소문자, 대문자, 숫자 중 최소 두 가지 이상 조합 필요")),
                                fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("멤버 핸드폰 번호"),
                                fieldWithPath("type").type(JsonFieldType.STRING).description("멤버 타입")
                                        .attributes(key("format").value("STUDENT, INSTRUCTOR 둘 중 하나"))

                        ),
                        responseFields(
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 강입한 멤버 아이디")
                        )))
                .when()
                .post("/api/members/sign-up")
                .then().log().all()
                .statusCode(200)
                .body("memberId", notNullValue());

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
                .post("/api/members/sign-up")
                .then()
                .extract()
                .response();

        int memberId = response.jsonPath().getInt("memberId");

        LoginRequest loginRequest = new LoginRequest("nag", "1q2w3e4r!");

        // when & then
        RestAssured
                .given(this.spec).log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .filter(document("{class-name}/{method-name}",
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("멤버 이름"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("멤버 암호")
                        ),
                        responseFields(
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("로그인한 멤버 아이디")
                        )))
                .body(loginRequest)
                .when()
                .post("/api/members/login")
                .then().log().all()
                .statusCode(200)
                .body("memberId", equalTo(memberId));
    }

}
