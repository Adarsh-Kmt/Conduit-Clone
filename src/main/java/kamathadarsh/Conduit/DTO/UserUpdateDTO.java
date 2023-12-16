package kamathadarsh.Conduit.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
public class UserUpdateDTO {

    private String emailId;
    private String password;
    private String imageLocation;
    private String bio;

}
