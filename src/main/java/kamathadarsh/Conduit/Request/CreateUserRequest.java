package kamathadarsh.Conduit.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import kamathadarsh.Conduit.CustomValidationAnnotations.PasswordStrengthCheck;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {

    @NotNull
    private String username;

    @PasswordStrengthCheck
    private String password;

    private String bio;

    @Email
    private String emailId;
}
