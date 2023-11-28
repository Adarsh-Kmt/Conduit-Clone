package kamathadarsh.Conduit.Response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProfileResponse extends CustomResponse{

    private String username;
    private String bio;
    private String image;
    private Boolean following;
}
