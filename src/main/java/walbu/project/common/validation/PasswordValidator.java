package walbu.project.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        if (password.length() < 6 || password.length() > 10) {
            return false;
        }

        int count = 0;
        boolean hasLowerCase = false;
        boolean hasUpperCase = false;
        boolean hasNumber = false;

        for (char c : password.toCharArray()) {
            if (c >= 97 && c <= 122) {
                hasLowerCase = true;
            } else if (c >= 65 && c <= 90) {
                hasUpperCase = true;
            } else if (c >= 48 && c <= 57) {
                hasNumber = true;
            }
        }
        if (hasLowerCase) count++;
        if (hasUpperCase) count++;
        if (hasNumber) count++;

        return count >= 2;
    }

}
