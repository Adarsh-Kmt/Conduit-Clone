package kamathadarsh.Conduit.CustomValidationAnnotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import kamathadarsh.Conduit.Validator.PasswordStrengthValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordStrengthValidator.class)
@Target(ElementType.FIELD)
public @interface PasswordStrengthCheck {

    public String message() default "password must have at least 1 digit, 1 uppercase, 1 lowercase and 1 special character.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
