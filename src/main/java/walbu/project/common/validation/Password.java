package walbu.project.common.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface Password {

    String message() default "비밀번호 형식이 올바르지 않습니다. 비밀번호는 최소 6자 이상 10자 이하이고 " +
            "영문 소문자, 대문자, 숫자 중 최소 두 가지 이상 조합 필요합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
