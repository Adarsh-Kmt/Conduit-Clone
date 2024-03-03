package kamathadarsh.Conduit.CustomValidationAnnotations;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import kamathadarsh.Conduit.Validator.ImageFileValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ImageFileValidator.class})
public @interface ValidImage {

    String message() default "invalid file type.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
