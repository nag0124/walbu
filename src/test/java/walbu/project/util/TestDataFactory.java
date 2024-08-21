package walbu.project.util;

import walbu.project.domain.lecture.data.Lecture;
import walbu.project.domain.member.data.Member;
import walbu.project.domain.member.data.MemberType;

public class TestDataFactory {

    public static Member createMember() {
        return new Member(
                "nag",
                "nag@walbu.com",
                "1q2w3e4r!",
                "01012341234",
                MemberType.STUDENT
        );
    }

    public static Member createSameNameMember(Member member) {
        return new Member(
                member.getName(),
                "another@walbu.com",
                "!4r3e2w1q",
                "01043214321",
                MemberType.INSTRUCTOR
        );
    }

    public static Lecture createLecture(Member instructor) {
        return new Lecture(
                instructor,
                "lecture",
                10000,
                10
        );
    }

    public static Lecture createSameNameLecture(Lecture lecture) {
        return new Lecture(
                lecture.getInstructor(),
                lecture.getName(),
                20000,
                20
        );
    }

}
