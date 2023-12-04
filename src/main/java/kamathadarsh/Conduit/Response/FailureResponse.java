package kamathadarsh.Conduit.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Builder
@Data
public class FailureResponse extends CustomResponse{


    private String message;
    private HttpStatus status;

}
