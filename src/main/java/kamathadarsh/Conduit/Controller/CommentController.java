package kamathadarsh.Conduit.Controller;

import kamathadarsh.Conduit.Request.CommentRequest;
import kamathadarsh.Conduit.Response.CustomResponse;
import kamathadarsh.Conduit.Response.FailureResponse;
import kamathadarsh.Conduit.Service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/api/articles/user/{username}/{slug}/comments")
    public ResponseEntity<CustomResponse> getAllCommentsUnderAnArticle
            (@PathVariable("username") String currUserUsername,
             @PathVariable("slug") String articleSlug)
    {

        CustomResponse response = commentService.getAllCommentsUnderAnArticle(currUserUsername, articleSlug);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)?HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);


    }

    @PostMapping("/api/articles/user/{username}/{slug}/comments")
    public ResponseEntity<CustomResponse> postComment(@PathVariable("username") String currUserUsername,
                                                      @PathVariable("slug") String articleSlug,
                                                      @RequestBody CommentRequest commentRequest)
    {

        CustomResponse response = commentService.postComment(currUserUsername, articleSlug, commentRequest);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);

    }

    @DeleteMapping("/api/articles/{slug}/comments/{id}")
    public ResponseEntity<CustomResponse> deleteComment(@PathVariable("slug") String articleSlug,
                                                @PathVariable("id") Long commentId)
    {

        CustomResponse response = commentService.deleteComment(articleSlug, commentId);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);
    }



}
