package walbu.project.common.error.exception;

import org.springframework.http.HttpStatus;

public class PasswordIsDifferentException extends ApiException {

    public PasswordIsDifferentException() {
        super(HttpStatus.UNAUTHORIZED, "로그인 할 수 없습니다. 비밀번호가 다릅니다.");
    }

}
