package walbu.project.common.error.exception;

import org.springframework.http.HttpStatus;

public class BearerPrefixNotIncludedException extends ApiException {

    public BearerPrefixNotIncludedException() {
        super(HttpStatus.BAD_REQUEST, "Bearer Prefix가 없습니다.");
    }

}
