package kamathadarsh.Conduit.Service;


import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.Article;

import kamathadarsh.Conduit.Exception.ArticleNotFoundException;


import kamathadarsh.Conduit.Request.GetArticleRequest;
import kamathadarsh.Conduit.Request.PostArticleRequest;
import kamathadarsh.Conduit.Request.UpdateArticleRequest;
import kamathadarsh.Conduit.Response.*;
import kamathadarsh.Conduit.jooqRepository.JOOQArticleRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.*;



@Service
@AllArgsConstructor

public class ArticleService {


    final JOOQArticleRepository jooqArticleRepository;

    final UserService userService;

    final CacheService cacheService;

    final TagService tagService;



    public List<ArticleResponse> getAllArticles(String currUserUsername,
                                                GetArticleRequest getArticleRequest)
    {

        List<Article> finalArticleList = jooqArticleRepository.globalFeed(currUserUsername, getArticleRequest);

        List<ArticleResponse> finalArticleResponseList = new ArrayList<>();

        for(Article article : finalArticleList){

            finalArticleResponseList.add(createArticleResponse(currUserUsername, article));
        }

        return finalArticleResponseList;


    }




    public ArticleResponse createArticle(String currUserUsername, PostArticleRequest postArticleRequest)
    {



        String finalSlug = slugify(postArticleRequest.getTitle());
        // creating the article record.
        Article newArticle = new Article(finalSlug,
                postArticleRequest.getBody(),
                LocalDateTime.now(),
                postArticleRequest.getDescription(),
                0,
                postArticleRequest.getTitle(),
                LocalDateTime.now(),
                currUserUsername);


        jooqArticleRepository.createArticle(newArticle);

        List<String> newTagList = postArticleRequest.getTagList();

        //---------------------------------------------------------------------------------------------------------
        // checking if each tag in the newTagList that comes with the postArticleRequest already exists.
        // if not, a new tag is created.

        if(postArticleRequest.getTagList() != null && !postArticleRequest.getTagList().isEmpty()){
            for(String tagName : newTagList){

                boolean tagExists = tagService.findTagByTagName(tagName);

                if(!tagExists){
                    tagService.createTag(tagName);
                }
                // adding the new article to the list of articles with a particular tag, for each tag in newTagList.
                tagService.addArticleToList(tagName, finalSlug);

            }
        }


        return createArticleResponse(currUserUsername, newArticle);


    }


    public CustomResponse getArticle(String currUserUsername, String articleSlug){

        try{

            CustomResponse response = cacheService.getArticleFromCacheIfAvailable(currUserUsername, articleSlug);

            if(response instanceof ArticleResponse) return response;

            Optional<Article> articleExists = jooqArticleRepository.findArticleBySlug(articleSlug);

            if(!articleExists.isPresent()) throw new ArticleNotFoundException("article with slug " + articleSlug + " was not found.");

            Article article = articleExists.get();

            cacheService.addArticleToCache(article);
            return createArticleResponse(currUserUsername, article);

        } catch (ArticleNotFoundException e) {

            return new FailureResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }


    }

    public ArticleResponse createArticleResponse(String currUserUsername, Article article){

        String authorUsername = article.getAuthorUsername();
        ProfileResponse authorProfile = (ProfileResponse) userService.getProfile(authorUsername, currUserUsername);
        return ArticleResponse.builder()
                .updatedAt(article.getUpdatedAt())
                .createdAt(article.getCreatedAt())
                .slug(article.getSlug())
                .title(article.getTitle())
                .body(article.getBody())
                .description(article.getDescription())
                .authorProfile(authorProfile)
                .favouriteCount(article.getFavouriteCount())
                .build();


    }

    public CustomResponse favouriteArticle(String currUserUsername, String articleSlug){

        try{
            boolean articleExists = jooqArticleRepository.checkIfArticleExistsByArticleSlug(articleSlug);

            if(!articleExists) throw new ArticleNotFoundException("article with slug " + articleSlug + " was not found.");

            if(jooqArticleRepository.articleIsFavouritedByUser(currUserUsername, articleSlug)){

                return FailureResponse.builder()
                        .message("you have already favourited this article.")
                        .status(HttpStatus.NOT_FOUND)
                        .build();
            }

            jooqArticleRepository.favouriteArticle(articleSlug, currUserUsername);

            Optional<Article> updatedArticle = jooqArticleRepository.findArticleBySlug(articleSlug);

            return createArticleResponse(currUserUsername, updatedArticle.get());


        }
        catch(ArticleNotFoundException e){

            return FailureResponse.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

    }

    public String slugify(String articleName){

        String slug = articleName.trim().toLowerCase().replace(" ", "-");
        return slug + "-" + UUID.randomUUID();
    }



    public CustomResponse unfavouriteArticle(String currUserUsername, String articleSlug){

        try{

            boolean articleExists = jooqArticleRepository.checkIfArticleExistsByArticleSlug(articleSlug);

            if(!articleExists) throw new ArticleNotFoundException("article with slug " + articleSlug + " was not found.");

            if(!jooqArticleRepository.articleIsFavouritedByUser(currUserUsername, articleSlug)){

                return FailureResponse.builder()
                        .message("user has not favourited the article")
                        .status(HttpStatus.NOT_FOUND)
                        .build();
            }

            jooqArticleRepository.unfavouriteArticle(articleSlug, currUserUsername);

            Optional<Article> updatedArticle = jooqArticleRepository.findArticleBySlug(articleSlug);

            return createArticleResponse(currUserUsername, updatedArticle.get());


        }
        catch(ArticleNotFoundException e){

            return FailureResponse.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    public CustomResponse updateArticle(String currUserUsername,
                                        String articleSlug,
                                        UpdateArticleRequest updateArticleRequest)
    {

        try{

            boolean articleExists = jooqArticleRepository.checkIfArticleExistsByArticleSlug(articleSlug);

            if(!articleExists) throw new ArticleNotFoundException("article with slug " + articleSlug + " not found");

            jooqArticleRepository.updateArticle(articleSlug, updateArticleRequest);

            Optional<Article> updatedArticleExists = jooqArticleRepository.findArticleBySlug(articleSlug);
            return createArticleResponse(currUserUsername, updatedArticleExists.get());

        }
        catch(ArticleNotFoundException e){
            return FailureResponse.builder().status(HttpStatus.NOT_FOUND).message(e.getMessage()).build();
        }


    }

    public CustomResponse deleteArticle(String currUserUsername, String articleSlug){

        try{

            Optional<Article> articleExists = jooqArticleRepository.findArticleBySlug(articleSlug);

            if(!articleExists.isPresent()) throw new ArticleNotFoundException("article with slug " + articleSlug + " not found.");

            Article article = articleExists.get();

            if(article.getAuthorUsername().equals(currUserUsername)) throw new ArticleNotFoundException("only author can delete article.");

            jooqArticleRepository.deleteArticle(articleSlug);

            return SuccessResponse.builder().successMessage("article has been deleted successfully.").build();


        }
        catch(ArticleNotFoundException e){

            return FailureResponse.builder().message(e.getMessage()).status(HttpStatus.NOT_FOUND).build();
        }
    }
}
