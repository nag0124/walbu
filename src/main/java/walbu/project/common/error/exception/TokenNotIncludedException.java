package walbu.project.common.error.exception;

import org.springframework.http.HttpStatus;

public class TokenNotIncludedException extends ApiException {


    public TokenNotIncludedException() {
        super(HttpStatus.BAD_REQUEST, "토큰이 없습니다.");
    }


}
