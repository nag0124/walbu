package walbu.project.common.error.exception;

import org.springframework.http.HttpStatus;

public class LectureNotFoundException extends ApiException {

    public LectureNotFoundException() {
        super(HttpStatus.NOT_FOUND, "강의를 찾을 수 없습니다.");
    }

}
