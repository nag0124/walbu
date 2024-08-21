package walbu.project.domain.lecture;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import walbu.project.domain.lecture.data.Lecture;
import walbu.project.domain.lecture.repository.LectureRepository;
import walbu.project.domain.member.data.Member;
import walbu.project.domain.member.repository.MemberRepository;
import walbu.project.util.TestDataFactory;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LectureRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LectureRepository lectureRepository;

    @Test
    @DisplayName("강의 엔티티를 저장하면 아이디가 부여된다.")
    void saveLectureAndGetId() {
        // given
        Member instructor = TestDataFactory.createMember();
        memberRepository.save(instructor);

        Lecture lecture = TestDataFactory.createLecture(instructor);

        // when
        Lecture savedLecture = lectureRepository.save(lecture);

        // then
        assertThat(savedLecture.getId()).isNotNull();
    }

    @Test
    @DisplayName("이름이 같은 강의를 저장하려고 하면 예외가 발생한다.")
    void saveSameNameLecture() {
        // given
        Member instructor = TestDataFactory.createMember();
        memberRepository.save(instructor);

        Lecture lecture = TestDataFactory.createLecture(instructor);
        lectureRepository.save(lecture);

        Lecture sameNameLecture = TestDataFactory.createSameNameLecture(lecture);

        // when & then
        assertThatThrownBy(() -> lectureRepository.save(sameNameLecture))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("강사가 없는 강의를 저장하려고 하면 예외가 발생한다.")
    void saveNoInstructorLecture() {
        // given
        Lecture lecture = TestDataFactory.createLecture(null);

        // when & then
        assertThatThrownBy(() -> lectureRepository.save(lecture))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

}
