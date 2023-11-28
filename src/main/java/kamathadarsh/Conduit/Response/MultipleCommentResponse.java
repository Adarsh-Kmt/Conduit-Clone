package kamathadarsh.Conduit.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class MultipleCommentResponse extends CustomResponse{

    private List<CommentResponse> comments;
}
