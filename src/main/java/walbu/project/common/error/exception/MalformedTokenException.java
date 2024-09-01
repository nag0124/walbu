package walbu.project.common.error.exception;

import org.springframework.http.HttpStatus;

public class MalformedTokenException extends ApiException {

    public MalformedTokenException() {
        super(HttpStatus.UNAUTHORIZED, "잘못된 형식의 토큰입니다.");
    }

}
