package walbu.project.domain.enrollment;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import walbu.project.domain.enrollment.data.dto.CreateEnrollmentRequest;
import walbu.project.domain.enrollment.data.dto.CreateEnrollmentResponse;
import walbu.project.domain.enrollment.repository.EnrollmentRepository;
import walbu.project.domain.enrollment.service.EnrollmentAsyncManager;
import walbu.project.domain.lecture.data.Lecture;
import walbu.project.domain.lecture.repository.LectureRepository;
import walbu.project.domain.member.data.Member;
import walbu.project.domain.member.data.MemberType;
import walbu.project.domain.member.repository.MemberRepository;
import walbu.project.util.TestDataFactory;

@SpringBootTest
@ActiveProfiles("test")
public class EnrollmentAsyncManagerTest {

    @Autowired
    EnrollmentAsyncManager enrollmentAsyncManager;

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
        List<CreateEnrollmentResponse> responses = enrollmentAsyncManager.createEnrollments(requests);

        // then
        long successCount = responses.stream()
                .filter(response -> response.getStatus().equals(HttpStatus.OK))
                .count();
        assertThat(successCount).isEqualTo(availableCount);
    }

    @Test
    @DisplayName("한 사람이 한번에 20개의 강의를 수강 신청할 때, 10개는 성공하고 10개는 실패한다.")
    void singleStudentEnrollTwentyLecturesAndSucceedTen() {
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

        List<Lecture> lectures = TestDataFactory.createLecturesWithZeroEnrollment(instructor, lectureCount,
                lectureCount - availableCount);
        lectureRepository.saveAll(lectures);

        List<CreateEnrollmentRequest> requests = TestDataFactory.createEnrollmentRequests(student, lectures);

        // when
        List<CreateEnrollmentResponse> responses = enrollmentAsyncManager.createEnrollments(requests);

        // then
        long successCount = responses.stream()
                .filter(response -> response.getStatus().equals(HttpStatus.OK))
                .count();
        long failCount = lectureCount - successCount;
        assertThat(successCount).isEqualTo(10);
        assertThat(failCount).isEqualTo(10);
    }

}
