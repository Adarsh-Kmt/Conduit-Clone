package kamathadarsh.Conduit.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Data
public class SuccessfulLoginResponse extends CustomResponse{

    private String JWTToken;
    private HttpStatus httpStatus;
}
