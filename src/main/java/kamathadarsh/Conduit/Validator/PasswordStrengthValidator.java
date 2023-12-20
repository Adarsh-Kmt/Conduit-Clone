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

//        PasswordValidator passwordValidator = new PasswordValidator(Arrays.asList(
//
//                new LengthRule(6, 30),
//
//                new CharacterRule(EnglishCharacterData.UpperCase, 1),
//
//                new CharacterRule(EnglishCharacterData.Special, 1),
//
//                new CharacterRule(EnglishCharacterData.Digit, 1),
//
//                new CharacterRule(EnglishCharacterData.LowerCase, 1),
//
//                new WhitespaceRule()
//        ));
//
//        RuleResult result = passwordValidator.validate(new PasswordData(password));
//
//        if(!result.isValid()){
//           return false;
//        }

        Matcher matcher = pattern.matcher(password);

        return matcher.matches();


    }
}
