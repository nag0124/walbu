package walbu.project.domain.enrollment;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import walbu.project.common.error.exception.ApiException;
import walbu.project.common.error.exception.InstructorCantEnrollHisLectureException;
import walbu.project.common.error.exception.LectureNotFoundException;
import walbu.project.common.error.exception.MemberEnrolledLectureException;
import walbu.project.domain.enrollment.data.Enrollment;
import walbu.project.domain.enrollment.data.EnrollmentResultType;
import walbu.project.domain.enrollment.data.dto.CreateEnrollmentRequest;
import walbu.project.domain.enrollment.data.dto.CreateEnrollmentResponse;
import walbu.project.domain.enrollment.repository.EnrollmentRepository;
import walbu.project.domain.enrollment.service.EnrollmentService;
import walbu.project.domain.lecture.data.Lecture;
import walbu.project.domain.lecture.repository.LectureRepository;
import walbu.project.domain.member.data.Member;
import walbu.project.domain.member.data.MemberType;
import walbu.project.domain.member.repository.MemberRepository;
import walbu.project.util.TestDataFactory;

@ActiveProfiles("test")
@SpringBootTest
public class EnrollmentServiceTest {

    @Autowired
    EnrollmentService enrollmentService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LectureRepository lectureRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @PersistenceContext
    EntityManager entityManager;

    @AfterEach
    void cleanUp() {
        enrollmentRepository.deleteAllInBatch();
        lectureRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("하나의 수강 신청이 성공한다.")
    void enrollSingleLecture() {
        // given
        Member student = new Member(
                "student",
                "student@walbu.com",
                "student1",
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

        Lecture lecture = new Lecture(
                instructor,
                "lecture",
                10000,
                1
        );
        lectureRepository.save(lecture);

        CreateEnrollmentRequest request = new CreateEnrollmentRequest(student.getId(), lecture.getId());

        // when
        CreateEnrollmentResponse response = enrollmentService.createEnrollment(request);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getLectureId()).isEqualTo(lecture.getId());
        assertThat(response.getMessage()).isEqualTo(EnrollmentResultType.SUCCESS.getMessage());
    }

    @Test
    @DisplayName("수강 강의 인원이 0이면 수강 신청이 실패한다.")
    void zeroAvailableCountFailsEnrollment() {
        // given
        Member student = new Member(
                "student",
                "student@walbu.com",
                "student1",
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

        Lecture lecture = new Lecture(
                instructor,
                "lecture",
                10000,
                0
        );
        lectureRepository.save(lecture);

        CreateEnrollmentRequest request = new CreateEnrollmentRequest(student.getId(), lecture.getId());

        // when
        CreateEnrollmentResponse response = enrollmentService.createEnrollment(request);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getLectureId()).isEqualTo(lecture.getId());
        assertThat(response.getMessage()).isEqualTo(EnrollmentResultType.FAIL.getMessage());
    }

    @Test
    @DisplayName("이미 수강한 강의에 수강 신청을 하면 예외가 발생한다.")
    void enrollEnrolledLecture() {
        // given
        Member student = new Member(
                "student",
                "student@walbu.com",
                "student1",
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

        Lecture lecture = new Lecture(
                instructor,
                "lecture",
                10000,
                1
        );
        lectureRepository.save(lecture);

        Enrollment enrollment = new Enrollment(student, lecture);
        enrollmentRepository.save(enrollment);

        CreateEnrollmentRequest request = new CreateEnrollmentRequest(student.getId(), lecture.getId());
        ApiException exception = new MemberEnrolledLectureException();

        // when
        assertThatThrownBy(() -> enrollmentService.createEnrollment(request))
                .isInstanceOf(MemberEnrolledLectureException.class)
                .hasMessage(exception.getMessage());
    }

    @Test
    @DisplayName("강사가 본인이 만든 강의를 수강 신청하면 예외가 발생한다.")
    void instructorEnrollHisLecture() {
        // given
        Member instructor = new Member(
                "instructor",
                "instructor@walbu.com",
                "instructor1",
                "01043214321",
                MemberType.INSTRUCTOR
        );
        memberRepository.save(instructor);

        Lecture lecture = new Lecture(
                instructor,
                "lecture",
                10000,
                1
        );
        lectureRepository.save(lecture);

        CreateEnrollmentRequest request = new CreateEnrollmentRequest(instructor.getId(), lecture.getId());
        ApiException exception = new InstructorCantEnrollHisLectureException();

        // when
        assertThatThrownBy(() -> enrollmentService.createEnrollment(request))
                .isInstanceOf(InstructorCantEnrollHisLectureException.class)
                .hasMessage(exception.getMessage());
    }

    @Test
    @DisplayName("강의를 수강하면 수강 가능 인원이 줄어든다.")
    void enrollmentDecresesAvailableCount() {
        // given
        Member student = new Member(
                "student",
                "student@walbu.com",
                "student1",
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

        Lecture lecture = new Lecture(
                instructor,
                "lecture",
                10000,
                10
        );
        lectureRepository.save(lecture);

        CreateEnrollmentRequest request = new CreateEnrollmentRequest(student.getId(), lecture.getId());

        // when
        enrollmentService.createEnrollment(request);

        // then
        Lecture byId = lectureRepository.findById(request.getLectureId()).orElseThrow(LectureNotFoundException::new);
        assertThat(byId.getAvailableCount()).isEqualTo(9);
    }

    @Test
    @DisplayName("20자리가 있는 강의의 동시적인 20개의 수강 신청이 다 성공한다.")
    void enroll20SeatLecture() throws InterruptedException {
        // given
        int studentCount = 20;
        int seatCount = 20;

        List<Member> students = TestDataFactory.createStudents(studentCount);
        memberRepository.saveAll(students);

        Member instructor = new Member(
                "instructor",
                "instructor@walbu.com",
                "instructor1",
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
        List<CreateEnrollmentResponse> responses = new CopyOnWriteArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(studentCount);
        CountDownLatch latch = new CountDownLatch(studentCount);

        // when
        for (CreateEnrollmentRequest request : requests) {
            executorService.submit(() -> {
                try {
                    CreateEnrollmentResponse response = enrollmentService.createEnrollment(request);
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
                .filter(response -> response.getStatus().equals(HttpStatus.OK))
                .count();
        assertThat(successCount).isEqualTo(seatCount);
        assertThat(afterEnrollment.getAvailableCount()).isZero();
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
                "instructor1",
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
        List<CreateEnrollmentResponse> responses = new CopyOnWriteArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(studentCount);
        CountDownLatch latch = new CountDownLatch(studentCount);

        // when
        for (CreateEnrollmentRequest request : requests) {
            executorService.submit(() -> {
                try {
                    CreateEnrollmentResponse response = enrollmentService.createEnrollment(request);
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
                .filter(response -> response.getStatus().equals(HttpStatus.OK))
                .count();
        long failCount = responses.size() - successCount;
        assertThat(successCount).isEqualTo(seatCount);
        assertThat(failCount).isEqualTo(studentCount - seatCount);
    }

}
