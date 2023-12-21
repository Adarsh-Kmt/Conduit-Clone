package kamathadarsh.Conduit.Controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kamathadarsh.Conduit.Request.GetArticleRequest;
import kamathadarsh.Conduit.Request.PostArticleRequest;
import kamathadarsh.Conduit.Request.UpdateArticleRequest;
import kamathadarsh.Conduit.Response.ArticleResponse;
import kamathadarsh.Conduit.Response.CustomResponse;
import kamathadarsh.Conduit.Response.FailureResponse;
import kamathadarsh.Conduit.Service.ArticleService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Validated
public class ArticleController {

    /*
    TODO:
        1) validate all article slugs. (not null)
        2) validate all usernames {follow/unfollow user}. (not null)
     */
    private final ArticleService articleService;

    @GetMapping("/api/user/{username}/articles")
    public ResponseEntity<List<ArticleResponse>> getListOfArticles(@PathVariable("username") String username,
                                                                   @RequestBody GetArticleRequest getArticleRequest)
    {


        List<ArticleResponse> articleResponseList = articleService.getAllArticles(username, getArticleRequest);

        return ResponseEntity.status(HttpStatus.OK).body(articleResponseList);


    }

    /*
    TODO:
        validate create article request:
        1) title of article - not null.
     */
    @PostMapping("/api/user/{username}/articles")
    public ResponseEntity<ArticleResponse> createArticle(@PathVariable("username") String currUserUsername,
                                                         @RequestBody @Valid PostArticleRequest postArticleRequest)
    {

        ArticleResponse response= articleService.createArticle(currUserUsername, postArticleRequest);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @GetMapping("/api/user/{username}/articles/{slug}")
    public ResponseEntity<CustomResponse> getArticle(@PathVariable("username") String currUserUsername,
                                                     @PathVariable("slug") @NotNull String slug)
    {

        CustomResponse response = articleService.getArticle(currUserUsername, slug);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);
    }

    @PostMapping("/api/user/{username}/articles/{slug}/favorite")
    public ResponseEntity<CustomResponse> favouriteArticle(@PathVariable("username") String currUserUsername,
                                                           @PathVariable("slug") @NotNull String articleSlug)
    {

        CustomResponse response = articleService.favouriteArticle(currUserUsername, articleSlug);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);


    }

    @DeleteMapping("/api/user/{username}/articles/{slug}/favorite")
    public ResponseEntity<CustomResponse> unfavouriteArticle(@PathVariable("username") String currUserUsername,
                                                             @PathVariable("slug") @NotNull String articleSlug)
    {
        CustomResponse response = articleService.unfavouriteArticle(currUserUsername, articleSlug);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);
    }

    @PutMapping("/api/user/{username}/articles/{slug}")
    public ResponseEntity<CustomResponse> updateArticle(@PathVariable("username") String currUserUsername,
                                                         @PathVariable("slug") @NotNull String articleSlug,
                                                         @RequestBody UpdateArticleRequest updateArticleRequest)
    {

        CustomResponse response = articleService.updateArticle(currUserUsername, articleSlug, updateArticleRequest);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);


    }

    @DeleteMapping("/api/user/{username}/articles/{slug}")
    public ResponseEntity<CustomResponse> deleteArticle(@PathVariable("username") String currUserUsername,
                                                        @PathVariable("slug") @NotNull String articleSLug)
    {

        CustomResponse response = articleService.deleteArticle(currUserUsername, articleSLug);

        HttpStatus responseToRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(responseToRequest).body(response);
    }



}
