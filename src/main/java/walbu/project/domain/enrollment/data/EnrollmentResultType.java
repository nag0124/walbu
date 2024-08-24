package walbu.project.domain.enrollment.data;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EnrollmentResultType {

    SUCCESS(HttpStatus.OK, "수강 신청에 성공했습니다."),
    FAIL(HttpStatus.CONFLICT, "수강 신청에 실패했습니다.");

    private final HttpStatus status;
    private final String message;

}
