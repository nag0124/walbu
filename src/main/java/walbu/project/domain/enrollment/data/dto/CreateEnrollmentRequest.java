package walbu.project.domain.enrollment.data.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class CreateEnrollmentRequest {

    @NotNull(message = "학생 아이디는 필수입니다.")
    @Positive(message = "학생 아이디는 1 이상의 숫자여야합니다.")
    private Long studentId;

    @NotNull(message = "강의 아이디는 필수입니다.")
    @Positive(message = "강의 아이디는 1 이상의 숫자여야합니다.")
    private Long lectureId;

}
