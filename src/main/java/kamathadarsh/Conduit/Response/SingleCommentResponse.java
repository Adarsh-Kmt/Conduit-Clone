package kamathadarsh.Conduit.Response;

import kamathadarsh.Conduit.Entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SingleCommentResponse extends CustomResponse{

    private CommentResponse comment;
}
