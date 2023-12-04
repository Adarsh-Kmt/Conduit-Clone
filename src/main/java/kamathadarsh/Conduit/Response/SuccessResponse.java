package kamathadarsh.Conduit.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SuccessResponse extends CustomResponse{

    private String successMessage;
}
