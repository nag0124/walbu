package walbu.project.common.error.exception;

import org.springframework.http.HttpStatus;

public class CantFindEncryptionAlgorithmException extends ApiException {

    public CantFindEncryptionAlgorithmException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "암호화 알고리즘을 찾을 수 없습니다.");
    }

}
