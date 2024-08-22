package walbu.project.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import walbu.project.domain.enrollment.data.Enrollment;
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

    public static List<Lecture> createLectures(Member instructor, int count) {
        return IntStream.range(0, count)
                .mapToObj(i ->
                        new Lecture(
                                instructor,
                                "name" + i,
                                10000,
                                10
                        ))
                .collect(Collectors.toUnmodifiableList());
    }

    public static Lecture createSameNameLecture(Lecture lecture) {
        return new Lecture(
                lecture.getInstructor(),
                lecture.getName(),
                20000,
                20
        );
    }

    public static List<Enrollment> createEnrollments(Member member, List<Lecture> lectures) {
        return lectures.stream()
                .map(lecture -> new Enrollment(member, lecture))
                .collect(Collectors.toUnmodifiableList());
    }

}
