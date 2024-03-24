package kamathadarsh.Conduit.Controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kamathadarsh.Conduit.Request.CommentRequest;
import kamathadarsh.Conduit.Response.CustomResponse;
import kamathadarsh.Conduit.Response.FailureResponse;
import kamathadarsh.Conduit.Service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Validated
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/api/articles/{slug}/comments")
    public ResponseEntity<CustomResponse> getAllCommentsUnderAnArticle(@PathVariable("slug") @NotNull String articleSlug)
    {

        CustomResponse response = commentService.getAllCommentsUnderAnArticle(articleSlug);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)?HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);


    }

    @PostMapping("/api/articles/{slug}/comments")
    public ResponseEntity<CustomResponse> postComment(@PathVariable("slug") @NotNull String articleSlug,
                                                      @RequestBody @Valid CommentRequest commentRequest)
    {

        CustomResponse response = commentService.postComment(articleSlug, commentRequest);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);

    }

    @DeleteMapping("/api/articles/{slug}/comments/{id}")
    public ResponseEntity<CustomResponse> deleteComment(@PathVariable("slug") @NotNull String articleSlug,
                                                @PathVariable("id") @NotNull Long commentId)
    {

        CustomResponse response = commentService.deleteComment(articleSlug, commentId);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);
    }


    @PostMapping("/api/articles/{slug}/comments/{id}/reply")
    public ResponseEntity<CustomResponse> replyToComment(@PathVariable("slug") @NotNull String articleSlug,
                                                         @PathVariable("id") @NotNull Long parentCommentId,
                                                         @RequestBody @Valid CommentRequest commentRequest)
    {

        CustomResponse response = commentService.replyToComment(articleSlug, parentCommentId, commentRequest);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);

    }

    @GetMapping("/api/articles/{slug}/comments/{id}/replies")
    public ResponseEntity<CustomResponse> getRepliesToComment(@PathVariable("slug") @NotNull String articleSlug,
                                                              @PathVariable("id") @NotNull Long parentCommentId){

        CustomResponse response = commentService.getRepliesToComment(articleSlug, parentCommentId);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);
    }



}
