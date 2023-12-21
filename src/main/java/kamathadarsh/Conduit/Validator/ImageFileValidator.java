package kamathadarsh.Conduit.Validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kamathadarsh.Conduit.CustomValidationAnnotations.ValidImage;
import org.springframework.web.multipart.MultipartFile;

public class ImageFileValidator implements ConstraintValidator<ValidImage, MultipartFile> {

    @Override
    public void initialize(ValidImage constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {

        boolean result = true;
        String imageContentType = multipartFile.getContentType();

        if(imageContentType != null && !correctContentType(imageContentType)) result = false;
        return result;
    }

    public boolean correctContentType(String imageContentType){

        return imageContentType.equals("image/png")
                || imageContentType.equals("image/jpg")
                || imageContentType.equals("image/jpeg");


    }


}
