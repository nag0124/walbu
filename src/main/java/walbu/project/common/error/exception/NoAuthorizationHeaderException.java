package walbu.project.common.error.exception;

import org.springframework.http.HttpStatus;

public class NoAuthorizationHeaderException extends ApiException {

    public NoAuthorizationHeaderException() {
        super(HttpStatus.UNAUTHORIZED, "Authorization Header가 없습니다.");
    }

}
