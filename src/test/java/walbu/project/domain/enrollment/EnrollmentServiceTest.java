package walbu.project.domain.enrollment;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import walbu.project.common.error.exception.ApiException;
import walbu.project.common.error.exception.InstructorCantEnrollHisLectureException;
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
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
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

}
