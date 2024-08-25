package walbu.project.domain.lecture;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import walbu.project.common.error.exception.ApiException;
import walbu.project.common.error.exception.MemberNotFoundException;
import walbu.project.common.error.exception.SameNameLectureExistsException;
import walbu.project.common.error.exception.StudentCantCreateLectureException;
import walbu.project.domain.lecture.data.Lecture;
import walbu.project.domain.lecture.data.dto.CreateLectureRequest;
import walbu.project.domain.lecture.data.dto.CreateLectureResponse;
import walbu.project.domain.lecture.repository.LectureRepository;
import walbu.project.domain.lecture.service.LectureService;
import walbu.project.domain.member.data.Member;
import walbu.project.domain.member.data.MemberType;
import walbu.project.domain.member.repository.MemberRepository;

@ActiveProfiles("test")
@SpringBootTest
public class LectureServiceTest {

    @Autowired
    LectureService lectureService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LectureRepository lectureRepository;

    @AfterEach
    void cleanUp() {
        lectureRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("강의를 생성한다.")
    void createLecture() {
        // given
        Member member = new Member(
                "nag",
                "nag@walbu.com"
                , "1q2w3e4r!",
                "01012341234",
                MemberType.INSTRUCTOR
        );
        memberRepository.save(member);

        CreateLectureRequest request = new CreateLectureRequest(
                member.getId(),
                "나그와 함께하는 부동산",
                10000,
                10
        );

        // when
        CreateLectureResponse response = lectureService.createLecture(request);

        // then
        assertThat(response.getLectureId()).isNotNull();
    }

    @Test
    @DisplayName("강의의 강사 아이디가 DB에 등록되지 않았다면 강의를 개설할 수 없다.")
    void notSavedInstructorCantMakeLecture() {
        // given
        CreateLectureRequest request = new CreateLectureRequest(
                1L,
                "나그와 함께하는 부동산",
                10000,
                10
        );
        ApiException exception = new MemberNotFoundException();

        // when & then
        assertThatThrownBy(() -> lectureService.createLecture(request))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessage(exception.getMessage());
    }

    @Test
    @DisplayName("이름이 같은 강의는 개설할 수 없다.")
    void cantCreateSameNameLecture() {
        // given
        Member member = new Member(
                "nag",
                "nag@walbu.com"
                , "1q2w3e4r!",
                "01012341234",
                MemberType.INSTRUCTOR
        );
        memberRepository.save(member);

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
        assertThatThrownBy(() -> lectureService.createLecture(request))
                .isInstanceOf(SameNameLectureExistsException.class)
                .hasMessage(exception.getMessage());
    }

    @Test
    @DisplayName("학생은 강의를 생성할 수 없다.")
    void studentCreatesLecture() {
        // given
        Member member = new Member(
                "nag",
                "nag@walbu.com"
                , "1q2w3e4r!",
                "01012341234",
                MemberType.STUDENT
        );
        memberRepository.save(member);

        CreateLectureRequest request = new CreateLectureRequest(
                member.getId(),
                "나그와 함께하는 부동산",
                10000,
                10
        );

        // when
        assertThatThrownBy(() -> lectureService.createLecture(request))
                .isInstanceOf(StudentCantCreateLectureException.class);
    }

}
