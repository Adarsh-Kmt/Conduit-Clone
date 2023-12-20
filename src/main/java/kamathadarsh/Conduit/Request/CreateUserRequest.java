package kamathadarsh.Conduit.Request;

import kamathadarsh.Conduit.CustomValidationAnnotations.PasswordStrengthCheck;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {

    private String username;

    @PasswordStrengthCheck
    private String password;

    private String bio;
    private String emailId;
}
