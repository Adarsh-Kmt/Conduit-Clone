package kamathadarsh.Conduit.Request;

import kamathadarsh.Conduit.Entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateRequest {

    private User user;
}
