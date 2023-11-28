package kamathadarsh.Conduit.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Builder
public class FailureResponse extends CustomResponse{

    private Throwable Exception;
    private String message;
    private HttpStatus status;

}
