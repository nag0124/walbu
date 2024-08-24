package walbu.project.common.error.exception;

import org.springframework.http.HttpStatus;

public class InstructorCantEnrollHisLectureException extends ApiException{

    public InstructorCantEnrollHisLectureException() {
        super(HttpStatus.BAD_REQUEST, "강사는 본인의 강의를 수강할 수 없습니다.");
    }

}
