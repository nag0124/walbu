package walbu.project.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import walbu.project.domain.enrollment.data.Enrollment;
import walbu.project.domain.enrollment.data.dto.CreateEnrollmentRequest;
import walbu.project.domain.enrollment.data.dto.CreateEnrollmentResponse;
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

    public static List<Member> createStudents(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> new Member(
                        "student" + i,
                        "student" + i + "@walbu.com",
                        "1q2w3e4r!",
                        "01012341234",
                        MemberType.STUDENT
                ))
                .collect(Collectors.toUnmodifiableList());
    }

    public static List<CreateEnrollmentRequest> createEnrollmentRequests(List<Member> students, Lecture lecture) {
        return students.stream()
                .map(student -> new CreateEnrollmentRequest(student.getId(), lecture.getId()))
                .collect(Collectors.toUnmodifiableList());
    }

    public static List<CreateEnrollmentRequest> createEnrollmentRequests(Member student, List<Lecture> lectures) {
        return lectures.stream()
                .map(lecture -> new CreateEnrollmentRequest(student.getId(), lecture.getId()))
                .collect(Collectors.toUnmodifiableList());
    }

    public static List<Lecture> createLecturesWithZeroEnrollment(Member instructor, int totalCount, int zeroCount) {
        return IntStream.rangeClosed(1, totalCount)
                .mapToObj(i -> {
                    int seatCount = i <= zeroCount ? 0 : 10;
                    return new Lecture(
                            instructor,
                            "Lecture " + i,
                            10000,
                            seatCount
                    );
                })
                .collect(Collectors.toUnmodifiableList());
    }


}
