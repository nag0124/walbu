package walbu.project.common.error.exception;

import org.springframework.http.HttpStatus;

public class MemberEnrolledLectureException extends ApiException {

    public MemberEnrolledLectureException() {
        super(HttpStatus.BAD_REQUEST, "멤버가 수강하고 있는 강의입니다.");
    }

}
