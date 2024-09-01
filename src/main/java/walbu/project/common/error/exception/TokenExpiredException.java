package walbu.project.common.error.exception;

import org.springframework.http.HttpStatus;

public class TokenExpiredException extends ApiException {

    public TokenExpiredException() {
        super(HttpStatus.FORBIDDEN, "토큰이 만료되었습니다.");
    }


}
