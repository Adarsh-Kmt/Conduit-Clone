package kamathadarsh.Conduit.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ProfileResponse extends CustomResponse implements Serializable {

    private String username;
    private String bio;
    private String image;
    private Boolean following;
}
