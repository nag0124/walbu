package walbu.project.domain.lecture.data.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import walbu.project.domain.lecture.data.Lecture;
import walbu.project.domain.member.data.Member;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class CreateLectureRequest {

    private Long instructorId;
    private String name;
    private Integer price;
    private Integer enrollmentCount;

    public Lecture toLecture(Member instructor) {
        return new Lecture(
                instructor,
                name,
                price,
                enrollmentCount
        );
    }

}
