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

    @GetMapping("/api/articles/feed")
    public ResponseEntity<List<ArticleResponse>> getListOfArticles(@RequestBody GetArticleRequest getArticleRequest)
    {


        List<ArticleResponse> articleResponseList = articleService.getAllArticles(getArticleRequest);

        return ResponseEntity.status(HttpStatus.OK).body(articleResponseList);


    }

    /*
    TODO:
        validate create article request:
        1) title of article - not null.
     */
    @PostMapping("/api/articles")
    public ResponseEntity<ArticleResponse> createArticle(@RequestBody @Valid PostArticleRequest postArticleRequest)
    {

        ArticleResponse response= articleService.createArticle(postArticleRequest);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @GetMapping("/api/articles/{slug}")
    public ResponseEntity<CustomResponse> getArticle(@PathVariable("slug") @NotNull String slug)
    {

        CustomResponse response = articleService.getArticle(slug);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);
    }

    @PostMapping("/api/articles/{slug}/favorite")
    public ResponseEntity<CustomResponse> favouriteArticle(@PathVariable("slug") @NotNull String articleSlug)
    {

        CustomResponse response = articleService.favouriteArticle(articleSlug);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);


    }

    @DeleteMapping("/api/articles/{slug}/unfavorite")
    public ResponseEntity<CustomResponse> unfavouriteArticle(@PathVariable("slug") @NotNull String articleSlug)
    {
        CustomResponse response = articleService.unfavouriteArticle(articleSlug);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);
    }

    @PutMapping("/api/articles/{slug}")
    public ResponseEntity<CustomResponse> updateArticle(@PathVariable("slug") @NotNull String articleSlug,
                                                         @RequestBody UpdateArticleRequest updateArticleRequest)
    {

        CustomResponse response = articleService.updateArticle(articleSlug, updateArticleRequest);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);


    }

    @DeleteMapping("/api/articles/{slug}")
    public ResponseEntity<CustomResponse> deleteArticle(@PathVariable("slug") @NotNull String articleSLug)
    {

        CustomResponse response = articleService.deleteArticle(articleSLug);

        HttpStatus responseToRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(responseToRequest).body(response);
    }



}
