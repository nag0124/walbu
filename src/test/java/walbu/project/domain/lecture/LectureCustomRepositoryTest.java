package walbu.project.domain.lecture;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import walbu.project.common.config.QueryDslConfig;
import walbu.project.domain.lecture.data.Lecture;
import walbu.project.domain.lecture.data.dto.ReadLectureResponse;
import walbu.project.domain.lecture.repository.LectureCustomRepositoryImpl;
import walbu.project.domain.lecture.repository.LectureRepository;
import walbu.project.domain.member.data.Member;
import walbu.project.domain.member.data.MemberType;
import walbu.project.domain.member.repository.MemberRepository;
import walbu.project.util.TestDataFactory;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QueryDslConfig.class)
public class LectureCustomRepositoryTest {

    @Autowired
    private LectureCustomRepositoryImpl lectureCustomRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Test
    @DisplayName("Lecture를 최근 등록순으로 가져온다.")
    void readLecturesInCreatedTimeOrder() {
        // given
        Member instructor = new Member(
                "instructor",
                "instructor@walbu.com"
                , "1q2w3e4r!",
                "01012341234",
                MemberType.INSTRUCTOR
        );
        memberRepository.save(instructor);

        List<Lecture> lectures = TestDataFactory.createLectures(instructor, 20);
        lectureRepository.saveAll(lectures);

        PageRequest request = PageRequest.of(0, 20, Sort.by("createdTime"));

        // when
        Page<ReadLectureResponse> responses = lectureCustomRepository.findPage(request);

        // then
        for (int i = 0; i < responses.getContent().size() - 1; i++) {
            ReadLectureResponse ahead = responses.getContent().get(i);
            ReadLectureResponse behind = responses.getContent().get(i + 1);

            assertThat(ahead.getLectureId()).isGreaterThan(behind.getLectureId());
        }
    }

    @Test
    @DisplayName("Lecture를 신청자 많은 순으로 가져온다.")
    void readLecturesInAssignedCountOrder() {
        // given
        Member instructor = new Member(
                "instructor",
                "instructor@walbu.com"
                , "1q2w3e4r!",
                "01012341234",
                MemberType.INSTRUCTOR
        );
        memberRepository.save(instructor);

        List<Lecture> lectures = TestDataFactory.createLectures(instructor, 20);
        makeAssignCountDistinct(lectures);
        lectureRepository.saveAll(lectures);

        PageRequest request = PageRequest.of(0, 20, Sort.by("assignedCount"));

        // when
        Page<ReadLectureResponse> responses = lectureCustomRepository.findPage(request);

        // then
        for (int i = 0; i < responses.getContent().size() - 1; i++) {
            ReadLectureResponse ahead = responses.getContent().get(i);
            ReadLectureResponse behind = responses.getContent().get(i + 1);

            assertThat(ahead.getAssignedCount()).isGreaterThan(behind.getAssignedCount());
        }
    }

    @Test
    @DisplayName("Lecture를 신청률 높은 순으로 가져온다.")
    void readLecturesInEnrollmentRateOrder() {
        // given
        Member instructor = new Member(
                "instructor",
                "instructor@walbu.com"
                , "1q2w3e4r!",
                "01012341234",
                MemberType.INSTRUCTOR
        );
        memberRepository.save(instructor);

        List<Lecture> lectures = TestDataFactory.createLectures(instructor, 20);
        makeAssignCountDistinct(lectures);
        lectureRepository.saveAll(lectures);

        PageRequest request = PageRequest.of(0, 20, Sort.by("enrollmentRate"));

        // when
        Page<ReadLectureResponse> responses = lectureCustomRepository.findPage(request);

        // then
        for (int i = 0; i < responses.getContent().size() - 1; i++) {
            ReadLectureResponse ahead = responses.getContent().get(i);
            float aheadRate = (float) ahead.getAssignedCount() / ahead.getEnrollmentCount();
            ReadLectureResponse behind = responses.getContent().get(i + 1);
            float behindRate = (float) behind.getAssignedCount() / behind.getEnrollmentCount();

            assertThat(aheadRate).isGreaterThan(behindRate);
        }
    }

    @Test
    @DisplayName("Lecture를 신청자 많은 순으로 가져왔을 때, 신청자가 같으면 최근 등록순으로 정렬된다.")
    void readLecturesInAssignedCountOrderWithSameCount() {
        // given
        Member instructor = new Member(
                "instructor",
                "instructor@walbu.com"
                , "1q2w3e4r!",
                "01012341234",
                MemberType.INSTRUCTOR
        );
        memberRepository.save(instructor);

        List<Lecture> lectures = TestDataFactory.createLectures(instructor, 20);
        makeAssignCountDuplicate(lectures);
        lectureRepository.saveAll(lectures);

        PageRequest request = PageRequest.of(0, 20, Sort.by("assignedCount"));

        // when
        Page<ReadLectureResponse> responses = lectureCustomRepository.findPage(request);

        // then
        for (int i = 0; i < responses.getContent().size() - 1; i++) {
            ReadLectureResponse ahead = responses.getContent().get(i);
            ReadLectureResponse behind = responses.getContent().get(i + 1);

            if (ahead.getAssignedCount() == behind.getAssignedCount()) {
                assertThat(ahead.getLectureId()).isGreaterThan(behind.getLectureId());
            }
            assertThat(ahead.getAssignedCount()).isGreaterThanOrEqualTo(behind.getAssignedCount());
        }
    }

    @Test
    @DisplayName("Lecture를 신청률 높은 순으로 가져왔을 때, 신청률이 같으면 최근 등록순으로 정렬된다.")
    void readLecturesInEnrollmentRateOrderWithSameRate() {
        // given
        Member instructor = new Member(
                "instructor",
                "instructor@walbu.com"
                , "1q2w3e4r!",
                "01012341234",
                MemberType.INSTRUCTOR
        );
        memberRepository.save(instructor);

        List<Lecture> lectures = TestDataFactory.createLectures(instructor, 20);
        makeAssignCountDuplicate(lectures);
        lectureRepository.saveAll(lectures);

        PageRequest request = PageRequest.of(0, 20, Sort.by("enrollmentRate"));

        // when
        Page<ReadLectureResponse> responses = lectureCustomRepository.findPage(request);

        // then
        for (int i = 0; i < responses.getContent().size() - 1; i++) {
            ReadLectureResponse ahead = responses.getContent().get(i);
            float aheadRate = (float) ahead.getAssignedCount() / ahead.getEnrollmentCount();
            ReadLectureResponse behind = responses.getContent().get(i + 1);
            float behindRate = (float) behind.getAssignedCount() / behind.getEnrollmentCount();

            if (aheadRate == behindRate) {
                assertThat(ahead.getLectureId()).isGreaterThan(behind.getLectureId());
            }
            assertThat(aheadRate).isGreaterThanOrEqualTo(behindRate);
        }
    }

    @Test
    @DisplayName("Lecture를 가져올 때, Sort 정보가 없으면 최근 등록순으로 가져온다.")
    void readLecturesWithoutSort() {
        // given
        Member instructor = new Member(
                "instructor",
                "instructor@walbu.com"
                , "1q2w3e4r!",
                "01012341234",
                MemberType.INSTRUCTOR
        );
        memberRepository.save(instructor);

        List<Lecture> lectures = TestDataFactory.createLectures(instructor, 20);
        lectureRepository.saveAll(lectures);

        PageRequest request = PageRequest.of(0, 20);

        // when
        Page<ReadLectureResponse> responses = lectureCustomRepository.findPage(request);

        // then
        for (int i = 0; i < responses.getContent().size() - 1; i++) {
            ReadLectureResponse ahead = responses.getContent().get(i);
            ReadLectureResponse behind = responses.getContent().get(i + 1);

            assertThat(ahead.getLectureId()).isGreaterThan(behind.getLectureId());
        }
    }

    @Test
    @DisplayName("Lecture를 가져올 때, Sort 정보를 사용할 수 없으면 최근 등록순으로 가져온다.")
    void readLecturesWithStrangeSort() {
        // given
        Member instructor = new Member(
                "instructor",
                "instructor@walbu.com"
                , "1q2w3e4r!",
                "01012341234",
                MemberType.INSTRUCTOR
        );
        memberRepository.save(instructor);

        List<Lecture> lectures = TestDataFactory.createLectures(instructor, 20);
        lectureRepository.saveAll(lectures);

        PageRequest request = PageRequest.of(0, 20, Sort.by("enrollmentCount"));

        // when
        Page<ReadLectureResponse> responses = lectureCustomRepository.findPage(request);

        // then
        for (int i = 0; i < responses.getContent().size() - 1; i++) {
            ReadLectureResponse ahead = responses.getContent().get(i);
            ReadLectureResponse behind = responses.getContent().get(i + 1);

            assertThat(ahead.getLectureId()).isGreaterThan(behind.getLectureId());
        }
    }

    @Test
    @DisplayName("Pageable을 사용하여 20개의 Lecture 중 19개를 가져온다.")
    void readNineteenLecturesAmonTwenty() {
        // given
        Member instructor = new Member(
                "instructor",
                "instructor@walbu.com"
                , "1q2w3e4r!",
                "01012341234",
                MemberType.INSTRUCTOR
        );
        memberRepository.save(instructor);

        List<Lecture> lectures = TestDataFactory.createLectures(instructor, 20);
        lectureRepository.saveAll(lectures);

        PageRequest request = PageRequest.of(0, 19, Sort.by("createdTime"));

        // when
        Page<ReadLectureResponse> responses = lectureCustomRepository.findPage(request);

        // then
        assertThat(responses.getTotalPages()).isEqualTo(2);
        assertThat(responses.getTotalElements()).isEqualTo(20);
        assertThat(responses.getContent().size()).isEqualTo(19);
    }

    private void makeAssignCountDistinct(List<Lecture> lectures) {
        for (int i = lectures.size() - 1; i >= 0; i--) {
            Lecture lecture = lectures.get(i);
            for (int j = 0; j <= i; j++) {
                lecture.assignSeat();
            }
        }
    }

    private void makeAssignCountDuplicate(List<Lecture> lectures) {
        for (int i = lectures.size() - 1; i >= 0; i--) {
            Lecture lecture = lectures.get(i);
            for (int j = 0; j <= i / 2; j++) {
                lecture.assignSeat();
            }
        }
    }

}
