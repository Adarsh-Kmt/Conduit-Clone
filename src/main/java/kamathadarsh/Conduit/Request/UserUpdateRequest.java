package kamathadarsh.Conduit.Request;

import jakarta.validation.constraints.Email;
import kamathadarsh.Conduit.CustomValidationAnnotations.PasswordStrengthCheck;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {

    @Email
    private String emailId;

    @PasswordStrengthCheck
    private String password;

    private String profilePictureActionString;
    private String bio;

}
