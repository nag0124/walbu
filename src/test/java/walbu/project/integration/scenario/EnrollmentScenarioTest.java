package walbu.project.integration.scenario;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

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

public class EnrollmentScenarioTest extends IntegrationTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LectureRepository lectureRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

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

    @Test
    @DisplayName("20자리가 있는 강의의 동시적인 20개의 수강 신청이 다 성공한다.")
    void enrollTwentySeatsAll() throws InterruptedException {
        // given
        int studentCount = 20;
        int seatCount = 20;

        List<Member> students = TestDataFactory.createStudents(studentCount);
        memberRepository.saveAll(students);

        Member instructor = new Member(
                "instructor",
                "instructor@walbu.com",
                "1q2w3e4r!",
                "01043214321",
                MemberType.INSTRUCTOR
        );
        memberRepository.save(instructor);

        Lecture lecture = new Lecture(
                instructor,
                "lecture",
                10000,
                seatCount
        );
        lectureRepository.save(lecture);

        List<CreateEnrollmentRequest> requests = TestDataFactory.createEnrollmentRequests(students, lecture);
        List<Response> responses = new CopyOnWriteArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(studentCount);
        CountDownLatch latch = new CountDownLatch(studentCount);

        // when
        for (CreateEnrollmentRequest request : requests) {
            executorService.submit(() -> {
                try {
                    Response response = RestAssured
                            .given().log().all()
                            .contentType(ContentType.JSON)
                            .accept(ContentType.JSON)
                            .body(request)
                            .when()
                            .post("/api/enrollments")
                            .then().log().all()
                            .extract().response();

                    responses.add(response);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        // then
        Lecture afterEnrollment = lectureRepository.findById(lecture.getId()).orElseThrow(LectureNotFoundException::new);
        long successCount = responses.stream()
                .filter(response -> response.getStatusCode() == HttpStatus.OK.value())
                .count();

        assertThat(afterEnrollment.getAssignedCount()).isEqualTo(successCount);
        assertThat(successCount).isEqualTo(seatCount);
    }

    @Test
    @DisplayName("6자리가 있는 강의의 동시적인 20개의 수강 신청 중에서 6개는 성공하고 14개는 실패한다.")
    void enrollSixSeats() throws InterruptedException {
        // given
        int studentCount = 20;
        int seatCount = 6;

        List<Member> students = TestDataFactory.createStudents(studentCount);
        memberRepository.saveAll(students);

        Member instructor = new Member(
                "instructor",
                "instructor@walbu.com",
                "1q2w3e4r!",
                "01043214321",
                MemberType.INSTRUCTOR
        );
        memberRepository.save(instructor);

        Lecture lecture = new Lecture(
                instructor,
                "lecture",
                10000,
                seatCount
        );
        lectureRepository.save(lecture);

        List<CreateEnrollmentRequest> requests = TestDataFactory.createEnrollmentRequests(students, lecture);
        List<Response> responses = new CopyOnWriteArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(studentCount);
        CountDownLatch latch = new CountDownLatch(studentCount);

        // when
        for (CreateEnrollmentRequest request : requests) {
            executorService.submit(() -> {
                try {
                    Response response = RestAssured
                            .given().log().all()
                            .contentType(ContentType.JSON)
                            .accept(ContentType.JSON)
                            .body(request)
                            .when()
                            .post("/api/enrollments")
                            .then().log().all()
                            .extract().response();

                    responses.add(response);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        // then
        long successCount = responses.stream()
                .filter(response -> response.getStatusCode() == HttpStatus.OK.value())
                .count();
        long failCount = responses.size() - successCount;

        assertThat(successCount).isEqualTo(seatCount);
        assertThat(failCount).isEqualTo(studentCount - seatCount);
    }

    @Test
    @DisplayName("한 사람의 한번에 20개의 강의 수강 신청이 다 성공한다.")
    void singleStudentEnrollTwentyLectures() {
        // given
        int lectureCount = 20;
        int availableCount = 20;

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

        List<Lecture> lectures = TestDataFactory.createLectures(instructor, lectureCount);
        lectureRepository.saveAll(lectures);

        List<CreateEnrollmentRequest> requests = TestDataFactory.createEnrollmentRequests(student, lectures);

        // when
        Response response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(requests)
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
        assertThat(successCount).isEqualTo(availableCount);
    }

}
