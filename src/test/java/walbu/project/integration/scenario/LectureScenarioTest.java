package walbu.project.integration.scenario;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import walbu.project.IntegrationTest;
import walbu.project.common.error.exception.ApiException;
import walbu.project.common.error.exception.SameNameLectureExistsException;
import walbu.project.common.jwt.JwtProvider;
import walbu.project.domain.lecture.data.Lecture;
import walbu.project.domain.lecture.data.dto.CreateLectureRequest;
import walbu.project.domain.lecture.repository.LectureRepository;
import walbu.project.domain.member.data.Member;
import walbu.project.domain.member.data.MemberType;
import walbu.project.domain.member.repository.MemberRepository;
import walbu.project.util.TestDataFactory;

public class LectureScenarioTest extends IntegrationTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LectureRepository lectureRepository;

    @Autowired
    JwtProvider jwtProvider;

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
        String token = jwtProvider.createToken(member.getId());

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
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
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
        String token = jwtProvider.createToken(member.getId());

        CreateLectureRequest request = new CreateLectureRequest(
                member.getId(),
                "나그와 함께하는 부동산",
                -1,
                10
        );

        // when & then
        RestAssured
                .given().log().all()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
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

    @Test
    @DisplayName("Lecture를 최근 등록순으로 가져온다.")
    void readLecturesInCreatedTimeOrder() {
        // given
        Member instructor = new Member(
                "instructor",
                "instructor@walbu.com",
                "1q2w3e4r!",
                "01012341234",
                MemberType.INSTRUCTOR
        );
        memberRepository.save(instructor);

        List<Lecture> lectures = TestDataFactory.createLectures(instructor, 20);
        lectureRepository.saveAll(lectures);

        // when & then
        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("page", 0)
                .queryParam("size", 20)
                .queryParam("sort", "createdTime")
                .get("/api/lectures")
                .then().log().all()
                .statusCode(200)
                .body("lectures.size()", is(20))
                .extract().response();

        List<Integer> lectureIds = response.jsonPath().getList("lectures.lectureId");
        for (int i = 0; i < lectureIds.size() - 1; i++) {
            assertThat(lectureIds.get(i)).isGreaterThan(lectureIds.get(i + 1));
        }
    }

    @Test
    @DisplayName("Lecture를 신청자 많은 순으로 가져온다.")
    void readLecturesInAssignedCountOrder() {
        // given
        Member instructor = new Member(
                "instructor",
                "instructor@walbu.com",
                "1q2w3e4r!",
                "01012341234",
                MemberType.INSTRUCTOR
        );
        memberRepository.save(instructor);

        List<Lecture> lectures = TestDataFactory.createLectures(instructor, 20);
        makeAssignCountDuplicate(lectures);
        lectureRepository.saveAll(lectures);

        // when & then
        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("page", 0)
                .queryParam("size", 20)
                .queryParam("sort", "assignedCount")
                .get("/api/lectures")
                .then().log().all()
                .statusCode(200)
                .body("lectures.size()", is(20))
                .extract().response();

        List<Integer> assignedCounts = response.jsonPath().getList("lectures.assignedCount");
        List<Integer> lectureIds = response.jsonPath().getList("lectures.lectureId");

        for (int i = 0; i < assignedCounts.size() - 1; i++) {
            int aheadAssignedCount = assignedCounts.get(i);
            int behindAssignedCount = assignedCounts.get(i + 1);
            int aheadLectureId = lectureIds.get(i);
            int behindLectureId = lectureIds.get(i + 1);

            if (aheadAssignedCount == behindAssignedCount) {
                assertThat(aheadLectureId).isGreaterThan(behindLectureId);
            }
            assertThat(aheadAssignedCount).isGreaterThanOrEqualTo(behindAssignedCount);
        }
    }

    @Test
    @DisplayName("Lecture를 신청률 높은 순으로 가져온다.")
    void readLecturesInEnrollmentRateOrder() {
        // given
        Member instructor = new Member(
                "instructor",
                "instructor@walbu.com",
                "1q2w3e4r!",
                "01012341234",
                MemberType.INSTRUCTOR
        );
        memberRepository.save(instructor);

        List<Lecture> lectures = TestDataFactory.createLectures(instructor, 20);
        makeAssignCountDuplicate(lectures);
        lectureRepository.saveAll(lectures);

        // when & then
        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("page", 0)
                .queryParam("size", 20)
                .queryParam("sort", "enrollmentRate")
                .get("/api/lectures")
                .then().log().all()
                .statusCode(200)
                .body("lectures.size()", is(20))
                .extract().response();

        List<Integer> assignedCounts = response.jsonPath().getList("lectures.assignedCount");
        List<Integer> enrollmentCounts = response.jsonPath().getList("lectures.enrollmentCount");
        List<Integer> lectureIds = response.jsonPath().getList("lectures.lectureId");

        for (int i = 0; i < assignedCounts.size() - 1; i++) {
            float aheadEnrollmentRate = (float) assignedCounts.get(i) / enrollmentCounts.get(i);
            float behindEnrollmentRate = (float) assignedCounts.get(i + 1) / enrollmentCounts.get(i + 1);
            int aheadLectureId = lectureIds.get(i);
            int behindLectureId = lectureIds.get(i + 1);

            if (aheadEnrollmentRate == behindEnrollmentRate) {
                assertThat(aheadLectureId).isGreaterThan(behindLectureId);
            }
            assertThat(aheadEnrollmentRate).isGreaterThanOrEqualTo(behindEnrollmentRate);
        }
    }

    private void makeAssignCountDuplicate(List<Lecture> lectures) {
        for (int i = lectures.size() - 1; i >= 0; i--) {
            Lecture lecture = lectures.get(i);
            for (int j = 0; j <= i / 2; j++) {
                lecture.assignSeat();
            }
        }
    }

}
