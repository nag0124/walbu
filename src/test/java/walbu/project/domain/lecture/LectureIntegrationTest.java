package walbu.project.domain.lecture;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import walbu.project.IntegrationTest;
import walbu.project.common.error.exception.ApiException;
import walbu.project.common.error.exception.SameNameLectureExistsException;
import walbu.project.domain.lecture.data.Lecture;
import walbu.project.domain.lecture.data.dto.CreateLectureRequest;
import walbu.project.domain.lecture.repository.LectureRepository;
import walbu.project.domain.member.data.Member;
import walbu.project.domain.member.data.MemberType;
import walbu.project.domain.member.repository.MemberRepository;

public class LectureIntegrationTest extends IntegrationTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LectureRepository lectureRepository;

    @Test
    @DisplayName("강의를 생성한다.")
    void createLecture() {
        // given
        Member member = new Member(
                "nag",
                "nag@walbu.com",
                "1q2w3e4r!",
                "01012341234",
                MemberType.STUDENT
        );
        memberRepository.save(member);

        CreateLectureRequest request = new CreateLectureRequest(
                member.getId(),
                "나그와 함께하는 부동산",
                10000,
                10
        );

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/lectures")
                .then().log().all()
                .statusCode(200)
                .body("lectureId", notNullValue());
    }

    @Test
    @DisplayName("이름이 같은 강의는 개설하려고 하면 예외가 발생한다.")
    void createSameNameLecture() {
        // given
        Member member = new Member(
                "nag",
                "nag@walbu.com",
                "1q2w3e4r!",
                "01012341234",
                MemberType.STUDENT
        );
        memberRepository.save(member);

        Lecture lecture = new Lecture(
                member,
                "나그와 함께하는 부동산",
                20000,
                20
        );
        lectureRepository.save(lecture);

        CreateLectureRequest request = new CreateLectureRequest(
                member.getId(),
                lecture.getName(),
                10000,
                10
        );
        ApiException exception = new SameNameLectureExistsException();

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/lectures")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("status", equalTo(exception.getStatus().name()))
                .body("message", equalTo(exception.getMessage()));
    }

    @Test
    @DisplayName("강의를 생성할 때 Request 유효성 검증에 실패하면 예외가 발생한다.")
    void failToValidateCreateLectureRequest() {
        // given
        Member member = new Member(
                "nag",
                "nag@walbu.com",
                "1q2w3e4r!",
                "01012341234",
                MemberType.STUDENT
        );
        memberRepository.save(member);

        CreateLectureRequest request = new CreateLectureRequest(
                member.getId(),
                "나그와 함께하는 부동산",
                -1,
                10
        );

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/lectures")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("status", equalTo(HttpStatus.BAD_REQUEST.name()))
                .body("message", equalTo("강의 가격은 최소 0원입니다."));
    }


}
