package walbu.project.common.error.exception;

import org.springframework.http.HttpStatus;

public class SameNameMemberExistsException extends ApiException{

    public SameNameMemberExistsException() {
        super(HttpStatus.BAD_REQUEST, "동일한 이름의 멤버가 존재합니다.");
    }

}
