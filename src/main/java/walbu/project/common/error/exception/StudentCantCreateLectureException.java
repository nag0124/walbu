package walbu.project.common.error.exception;

import org.springframework.http.HttpStatus;

public class StudentCantCreateLectureException extends ApiException {

    public StudentCantCreateLectureException() {
        super(HttpStatus.FORBIDDEN, "학생은 강의를 만들 수 없습니다.");
    }

}
