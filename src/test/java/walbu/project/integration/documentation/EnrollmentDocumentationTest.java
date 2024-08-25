package walbu.project.integration.documentation;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.JsonFieldType;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import walbu.project.IntegrationTest;
import walbu.project.common.error.exception.LectureNotFoundException;
import walbu.project.domain.enrollment.data.EnrollmentResultType;
import walbu.project.domain.enrollment.data.dto.CreateEnrollmentRequest;
import walbu.project.domain.enrollment.repository.EnrollmentRepository;
import walbu.project.domain.lecture.data.Lecture;
import walbu.project.domain.lecture.repository.LectureRepository;
import walbu.project.domain.member.data.Member;
import walbu.project.domain.member.data.MemberType;
import walbu.project.domain.member.repository.MemberRepository;
import walbu.project.util.TestDataFactory;

public class EnrollmentDocumentationTest extends IntegrationTest {

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
                .given(this.spec).log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
                .filter(document("{class-name}/{method-name}",
                        requestFields(
                                fieldWithPath("studentId").description("멤버 아이디"),
                                fieldWithPath("lectureId").description("강의 아이디")
                        ),
                        responseFields(
                                fieldWithPath("lectureId").type(JsonFieldType.NUMBER).description("수강 신청한 강의 아이디"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("수강 신청 결과 메세지")
                        )))
                .when()
                .post("/api/enrollments")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("lectureId", equalTo(lecture.getId().intValue()))
                .body("message", equalTo(EnrollmentResultType.SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("한 사람이 한번에 20개의 강의를 수강 신청할 때, 10개는 성공하고 10개는 실패한다.")
    void createEnrollments() {
        // given
        int lectureCount = 20;
        int availableCount = 10;

        Member student = new Member(
                "student",
                "student@walbu.com",
                "1q2w3e4r!",
                "01012341234",
                MemberType.STUDENT
        );
        memberRepository.save(student);

        Member instructor = new Member(
                "instructor",
                "instructor@walbu.com",
                "instructor1",
                "01043214321",
                MemberType.INSTRUCTOR
        );
        memberRepository.save(instructor);

        List<Lecture> lectures = TestDataFactory.createLecturesWithZeroEnrollment(instructor, lectureCount, lectureCount - availableCount);
        lectureRepository.saveAll(lectures);

        List<CreateEnrollmentRequest> requests = TestDataFactory.createEnrollmentRequests(student, lectures);

        // when
        Response response = RestAssured
                .given(this.spec).log().all()
                .contentType(ContentType.JSON)
                .body(requests)
                .filter(document("{class-name}/{method-name}",
                        requestFields(
                                fieldWithPath("[].studentId").description("멤버 아이디"),
                                fieldWithPath("[].lectureId").description("강의 아이디")
                        ),
                        responseFields(
                                fieldWithPath("[].lectureId").type(JsonFieldType.NUMBER).description("수강 신청한 강의 아이디"),
                                fieldWithPath("[].message").type(JsonFieldType.STRING).description("수강 신청 결과 메세지")
                        )))
                .when()
                .post("/api/enrollments/batch")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        // then
        List<String> messages = response.jsonPath().getList("message");
        long successCount = messages.stream()
                .filter(message -> message.equals(EnrollmentResultType.SUCCESS.getMessage()))
                .count();
        long failCount = lectureCount - successCount;
        assertThat(successCount).isEqualTo(availableCount);
        assertThat(failCount).isEqualTo(lectureCount - availableCount);
    }

}

