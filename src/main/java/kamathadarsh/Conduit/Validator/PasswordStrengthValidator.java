package kamathadarsh.Conduit.Validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kamathadarsh.Conduit.CustomValidationAnnotations.PasswordStrengthCheck;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PasswordStrengthValidator implements ConstraintValidator<PasswordStrengthCheck, String> {

    public static final String patternString = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    public static final Pattern pattern = Pattern.compile(patternString);

    @Override
    public void initialize(PasswordStrengthCheck constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {

        Matcher matcher = pattern.matcher(password);

        return matcher.matches();


    }
}
