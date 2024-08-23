package walbu.project.domain.member;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import walbu.project.IntegrationTest;
import walbu.project.domain.member.data.MemberType;
import walbu.project.domain.member.data.dto.CreateMemberRequest;

public class MemberIntegrationTest extends IntegrationTest {

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

}
