package kamathadarsh.Conduit.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CommentRequest {

    @NotNull(message = "comment body must not be null.")
    @NotBlank(message = "comment body must not be blank.")
    private String body;
}
