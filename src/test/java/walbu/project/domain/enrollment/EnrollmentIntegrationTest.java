package walbu.project.domain.enrollment;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import walbu.project.IntegrationTest;
import walbu.project.domain.enrollment.data.EnrollmentResultType;
import walbu.project.domain.enrollment.data.dto.CreateEnrollmentRequest;
import walbu.project.domain.enrollment.repository.EnrollmentRepository;
import walbu.project.domain.lecture.data.Lecture;
import walbu.project.domain.lecture.repository.LectureRepository;
import walbu.project.domain.member.data.Member;
import walbu.project.domain.member.data.MemberType;
import walbu.project.domain.member.repository.MemberRepository;

public class EnrollmentIntegrationTest extends IntegrationTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LectureRepository lectureRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Test
    @DisplayName("한 강의의 수강 신청에 성공한다.")
    void createEnrollment() {
        Member instructor = new Member(
                "instructor",
                "instructor@walbu.com",
                "1q2w3e4r!",
                "01012341234",
                MemberType.INSTRUCTOR
        );
        memberRepository.save(instructor);

        Member student = new Member(
                "student",
                "student@walbu.com",
                "1q2w3e4r!",
                "01043214321",
                MemberType.STUDENT
        );
        memberRepository.save(student);

        Lecture lecture = new Lecture(
                instructor,
                "lecture",
                10000,
                10
        );
        lectureRepository.save(lecture);

        CreateEnrollmentRequest request = new CreateEnrollmentRequest(
                student.getId(),
                lecture.getId()
        );

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/enrollments")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("lectureId", equalTo(lecture.getId().intValue()))
                .body("message", equalTo(EnrollmentResultType.SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("한 강의의 수강 신청에 실패한다.")
    void failToCreateEnrollment() {
        Member instructor = new Member(
                "instructor",
                "instructor@walbu.com",
                "1q2w3e4r!",
                "01012341234",
                MemberType.INSTRUCTOR
        );
        memberRepository.save(instructor);

        Member student = new Member(
                "student",
                "student@walbu.com",
                "1q2w3e4r!",
                "01043214321",
                MemberType.STUDENT
        );
        memberRepository.save(student);

        Lecture lecture = new Lecture(
                instructor,
                "lecture",
                10000,
                0
        );
        lectureRepository.save(lecture);

        CreateEnrollmentRequest request = new CreateEnrollmentRequest(
                student.getId(),
                lecture.getId()
        );

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/enrollments")
                .then().log().all()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("lectureId", equalTo(lecture.getId().intValue()))
                .body("message", equalTo(EnrollmentResultType.FAIL.getMessage()));
    }

    @Test
    @DisplayName("수강 신청 Request가 유효성 검증에 실패한다.")
    void failToValidateCreateEnrollmentRequest() {
        CreateEnrollmentRequest request = new CreateEnrollmentRequest(
                0L,
                1L
        );

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/enrollments")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

}
