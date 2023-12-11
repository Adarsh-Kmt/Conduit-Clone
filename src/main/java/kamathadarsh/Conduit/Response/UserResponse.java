package kamathadarsh.Conduit.Response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserResponse extends CustomResponse{

    private String username;
    private String email;
    private String bio;
    private byte[] image;
}
