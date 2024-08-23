package walbu.project.domain.lecture.data.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

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

    @NotNull(message = "강사 아이디는 필수입니다.")
    @Positive(message = "강사 아이디는 1 이상의 숫자여야합니다.")
    private Long instructorId;

    @NotBlank(message = "강의 이름은 빈칸일 수 없습니다.")
    private String name;

    @NotNull(message = "강의 가격은 필수입니다.")
    @PositiveOrZero(message = "강의 가격은 최소 0원입니다.")
    private Integer price;

    @NotNull(message = "수강 인원은 필수입니다.")
    @Positive(message = "수강 인원은 최소 1명입니다.")
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
