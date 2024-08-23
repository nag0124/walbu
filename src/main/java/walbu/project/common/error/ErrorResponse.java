package walbu.project.common.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import walbu.project.common.error.exception.ApiException;

@Getter
public class ErrorResponse {

    private final HttpStatus status;
    private final String message;

    private ErrorResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public static ErrorResponse from(ApiException exception) {
        return new ErrorResponse(exception.getStatus(), exception.getMessage());
    }

}
