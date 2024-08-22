package walbu.project.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import walbu.project.domain.enrollment.data.Enrollment;
import walbu.project.domain.enrollment.repository.EnrollmentRepository;
import walbu.project.domain.lecture.data.Lecture;
import walbu.project.domain.lecture.repository.LectureRepository;
import walbu.project.domain.member.data.Member;
import walbu.project.domain.member.repository.MemberRepository;
import walbu.project.util.TestDataFactory;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EntityMappingTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("멤버가 강의를 만들고 수강 정보가 있을 때, 멤버를 삭제하면 멤버의 강의와 수강 정보가 다 삭제된다.")
    void deleteAllWithMember() {
        // given
        Member member = TestDataFactory.createMember();
        memberRepository.save(member);
        Long memberId = member.getId();

        List<Lecture> lectures = TestDataFactory.createLectures(member, 3);
        lectureRepository.saveAll(lectures);

        List<Enrollment> enrollments = TestDataFactory.createEnrollments(member, lectures);
        enrollmentRepository.saveAll(enrollments);

        entityManager.flush();
        entityManager.clear();

        // when
        memberRepository.deleteById(memberId);

        // then
        List<Lecture> memberLectures = lectureRepository.findAllByInstructorId(memberId);
        List<Enrollment> memberEnrollments = enrollmentRepository.findAllByMemberId(memberId);

        assertThat(memberLectures).isEmpty();
        assertThat(memberEnrollments).isEmpty();
    }

    @Test
    @DisplayName("강의를 수강하고 있는 멤버가 있을 때, 강의를 삭제하면 수강 정보가 다 삭제된다.")
    void deletingLectureDeletesEnrollments() {
        // given
        Member member = TestDataFactory.createMember();
        memberRepository.save(member);

        Lecture lecture = TestDataFactory.createLecture(member);
        lectureRepository.save(lecture);
        Long lectureId = lecture.getId();

        Enrollment enrollment = new Enrollment(member, lecture);
        enrollmentRepository.save(enrollment);

        entityManager.flush();
        entityManager.clear();

        // when
        lectureRepository.deleteById(lectureId);

        // then
        List<Enrollment> lectureEnrollments = enrollmentRepository.findAllByLectureId(lectureId);

        assertThat(lectureEnrollments).isEmpty();
    }

}


