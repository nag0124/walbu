package walbu.project.common.error.exception;

import org.springframework.http.HttpStatus;

public class SameNameLectureExistsException extends ApiException {

    public SameNameLectureExistsException() {
        super(HttpStatus.BAD_REQUEST, "동일한 이름의 강의가 존재합니다.");
    }
}
