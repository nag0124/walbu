package walbu.project.common.error;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

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

    public static ErrorResponse from(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldError();

        if (fieldError != null) {
            return new ErrorResponse(HttpStatus.BAD_REQUEST, fieldError.getDefaultMessage());
        }
        return new ErrorResponse(HttpStatus.BAD_REQUEST, "입력 값이 유효하지 않습니다.");
    }

}
