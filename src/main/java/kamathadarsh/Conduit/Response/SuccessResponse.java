package kamathadarsh.Conduit.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponse extends CustomResponse{

    private String successMessage;
}
