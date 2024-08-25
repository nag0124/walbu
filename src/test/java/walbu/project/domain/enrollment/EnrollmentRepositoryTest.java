package walbu.project.domain.enrollment;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import walbu.project.common.config.QueryDslConfig;
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
@Import(QueryDslConfig.class)
public class EnrollmentRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Test
    @DisplayName("수강 엔티티를 저장하면 아이디가 부여된다.")
    void saveEnrollmentAndGetId() {
        // given
        Member member = TestDataFactory.createMember();
        memberRepository.save(member);

        Lecture lecture = TestDataFactory.createLecture(member);
        lectureRepository.save(lecture);

        Enrollment enrollment = new Enrollment(member, lecture);

        // when
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        // then
        assertThat(savedEnrollment.getId()).isNotNull();
    }

    @Test
    @DisplayName("멤버 엔티티가 없는 수강 엔티티를 저장하려고 하면 예외가 발생한다.")
    void saveNoMemberEnrollment() {
        // given
        Member member = TestDataFactory.createMember();
        memberRepository.save(member);

        Lecture lecture = TestDataFactory.createLecture(member);
        lectureRepository.save(lecture);

        Enrollment enrollment = new Enrollment(null, lecture);

        // when & then
        assertThatThrownBy(() -> enrollmentRepository.save(enrollment))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("강의 엔티티가 없는 수강 엔티티를 저장하려고 하면 예외가 발생한다.")
    void saveNoLectureEnrollment() {
        // given
        Member member = TestDataFactory.createMember();
        memberRepository.save(member);

        Lecture lecture = TestDataFactory.createLecture(member);
        lectureRepository.save(lecture);

        Enrollment enrollment = new Enrollment(member, null);

        // when & then
        assertThatThrownBy(() -> enrollmentRepository.save(enrollment))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

}
