package walbu.project.domain.lecture;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import walbu.project.domain.lecture.data.Lecture;
import walbu.project.domain.member.data.Member;
import walbu.project.domain.member.data.MemberType;

public class LectureTest {

    @Test
    @DisplayName("강의 수강이 할당되면 assignedCount가 1 증가한다.")
    void assignSeatAddsOneToAssignedCount() {
        // given
        Member instructor = new Member(
                "nag",
                "nag@walbu.com",
                "1q2w3e4r!",
                "01012341234",
                MemberType.INSTRUCTOR
        );

        Lecture lecture = new Lecture(
                instructor,
                "lecture",
                10000,
                10
        );

        Integer assignedCount = lecture.getAssignedCount();

        // when
        boolean b = lecture.assignSeat();

        // then
        assertThat(b).isTrue();
        assertThat(lecture.getAssignedCount()).isEqualTo(assignedCount + 1);
    }

    @Test
    @DisplayName("강의 수강이 할당되면 enrollmentRate가 증가한다.")
    void assignSeatIncreasesEnrollmentRate() {
        // given
        Member instructor = new Member(
                "nag",
                "nag@walbu.com",
                "1q2w3e4r!",
                "01012341234",
                MemberType.INSTRUCTOR
        );

        Lecture lecture = new Lecture(
                instructor,
                "lecture",
                10000,
                10
        );

        Float enrollmentRate = lecture.getEnrollmentRate();

        // when
        boolean b = lecture.assignSeat();

        // then
        assertThat(b).isTrue();
        assertThat(lecture.getEnrollmentRate()).isGreaterThan(enrollmentRate);
    }

    @Test
    @DisplayName("강의가 다 할당되면 enrollmentRate가 1.0f이다.")
    void assignAllSeatEnrollmentRateIsOne() {
        // given
        Member instructor = new Member(
                "nag",
                "nag@walbu.com",
                "1q2w3e4r!",
                "01012341234",
                MemberType.INSTRUCTOR
        );

        Lecture lecture = new Lecture(
                instructor,
                "lecture",
                10000,
                17
        );
        for (int i = 0; i < 16; i++) {
            lecture.assignSeat();
        }

        // when
        boolean b = lecture.assignSeat();

        // then
        assertThat(b).isTrue();
        assertThat(lecture.getEnrollmentRate()).isEqualTo(1.0f);
    }

    @Test
    @DisplayName("강의가 다 할당되면 assignSeat()이 실패한다.")
    void assignAllSeatThenAssignFails() {
        // given
        Member instructor = new Member(
                "nag",
                "nag@walbu.com",
                "1q2w3e4r!",
                "01012341234",
                MemberType.INSTRUCTOR
        );

        Lecture lecture = new Lecture(
                instructor,
                "lecture",
                10000,
                17
        );
        for (int i = 0; i < 17; i++) {
            lecture.assignSeat();
        }

        // when
        boolean b = lecture.assignSeat();

        // then
        assertThat(b).isFalse();
    }

}
