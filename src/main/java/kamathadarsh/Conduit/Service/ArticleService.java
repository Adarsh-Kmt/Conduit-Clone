package kamathadarsh.Conduit.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import kamathadarsh.Conduit.Entity.Article;
import kamathadarsh.Conduit.Entity.Tag;
import kamathadarsh.Conduit.Entity.User;
import kamathadarsh.Conduit.Exception.ArticleNotFoundException;
import kamathadarsh.Conduit.Repository.ArticleRepository;
import kamathadarsh.Conduit.Repository.TagRepository;
import kamathadarsh.Conduit.Repository.UserRepository;
import kamathadarsh.Conduit.Request.GetArticleRequest;
import kamathadarsh.Conduit.Request.PostArticleRequest;
import kamathadarsh.Conduit.Response.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;



@Service
@AllArgsConstructor

public class ArticleService {


    final ArticleRepository articleRepository;

    final UserService userService;

    final CacheService cacheService;

    final EntityManager entityManager;

    final UserRepository userRepository;

    final TagService tagService;

    final TagRepository tagRepository;
    public List<ArticleResponse> getAllArticles(String currUserUsername, GetArticleRequest getArticleRequest){

        //------------------------------------------------------------------------------------------------------
        // using Criteria to get Articles according to articleRequest

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Article> criteriaQuery = cb.createQuery(Article.class);

        List<Predicate> finalPredicateList = new ArrayList<>();

        Root<Article> articleRoot = criteriaQuery.from(Article.class);

        if(getArticleRequest.getTags() != null && !getArticleRequest.getTags().isEmpty()){

            Join<Article, Tag> articleTagJoin = articleRoot.join("tags", JoinType.INNER);
            System.out.println("tag predicate applied");
            Predicate tagsPredicate = articleTagJoin.get("tagName").in(getArticleRequest.getTags());

            finalPredicateList.add(tagsPredicate);


        }




        if(getArticleRequest.getIsFavourited() != null && getArticleRequest.getIsFavourited()){



            System.out.println("favourited predicate applied");

            Join<Article, User> favouriteArticleUserJoin = articleRoot.join("favouriteByList", JoinType.INNER);
            Predicate favouriteOrNotPredicate = cb.equal(favouriteArticleUserJoin.get("username"), currUserUsername);

            finalPredicateList.add(favouriteOrNotPredicate);

        }
        if(getArticleRequest.getAuthorUsername() != null && !getArticleRequest.getAuthorUsername().isBlank()){

            System.out.println("author predicated applied");
            Join<Article, User> articleAuthorUserJoin = articleRoot.join("author", JoinType.INNER);
            Predicate authorPredicate = cb.equal(articleAuthorUserJoin.get("username"), getArticleRequest.getAuthorUsername());

            finalPredicateList.add(authorPredicate);


        }
        Predicate finalPredicate = null;

        for(int i = 0; i < finalPredicateList.size(); i++){

            if(i == 0) finalPredicate = finalPredicateList.get(0);

            else{
                finalPredicate = cb.and(finalPredicate, finalPredicateList.get(i));
            }
        }

        criteriaQuery.where(finalPredicate);

        TypedQuery<Article> articleTypedQuery = entityManager.createQuery(criteriaQuery);

        List<Article> finalArticleList = articleTypedQuery.getResultList();

        List<ArticleResponse> finalArticleResponseList = new ArrayList<>();

        for(Article article : finalArticleList){

            finalArticleResponseList.add(createArticleResponse(currUserUsername, article));
        }

        return finalArticleResponseList;


    }

    public ArticleResponse createArticle(String currUserUsername, PostArticleRequest postArticleRequest){


        User author = userRepository.findByUsername(currUserUsername).get();

        List<String> newTagList = postArticleRequest.getTagList();

        Set<Tag> finalTagList = new HashSet<>();

        //---------------------------------------------------------------------------------------------------------
        // checking if each tag in the newTagList that comes with the postArticleRequest already exists.
        // if not, a new tag is created.
        // all tags are added to the finalTagList, which will eventually be used as value for tags field of the
        // article.

        for(String tagName : newTagList){

            Optional<Tag> tagExists = tagRepository.findTagByTagName(tagName);

            if(tagExists.isPresent() == false){

                Tag newTag = tagService.createTag(tagName);

                finalTagList.add(newTag);

            }
            else finalTagList.add(tagExists.get());
        }
        //---------------------------------------------------------------------------------------------------------
        // creating the article record.

        System.out.println("slug of current article is : " + slugify2(postArticleRequest.getTitle()));

        Article newArticle = Article.builder()
                .title(postArticleRequest.getTitle())
                .slug(slugify2(postArticleRequest.getTitle()))
                .body(postArticleRequest.getBody())
                .author(author)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .description(postArticleRequest.getDescription())
                .favouriteByList(new HashSet<>())
                .favouriteCount(0)
                .tags(finalTagList)
                .build();

        articleRepository.save(newArticle);


        //---------------------------------------------------------------------------------------------------------
        // adding the new article to the list of articles with a particular tag, for each tag in newTagList.
        // newTagList is the list that was sent in the postArticleRequest

        for(String tagName : newTagList){

           tagService.addArticleToList(tagService.findTagByTagName(tagName).get(), newArticle);


        }
        //---------------------------------------------------------------------------------------------------------


        return createArticleResponse(currUserUsername, newArticle);


    }


    public CustomResponse getArticle(String currUserUsername, String articleSlug){

        try{

            CustomResponse response = cacheService.getArticleFromCacheIfAvailable(currUserUsername, articleSlug);

            if(response instanceof ArticleResponse) return response;

            Optional<Article> articleExists = articleRepository.findArticleBySlug(articleSlug);

            if(!articleExists.isPresent()) throw new ArticleNotFoundException("article with slug " + articleSlug + " was not found.");

            Article article = articleExists.get();

            cacheService.addArticleToCache(article);
            return createArticleResponse(currUserUsername, article);

        } catch (ArticleNotFoundException e) {

            return new FailureResponse(e, e.getMessage(), HttpStatus.NOT_FOUND);
        }


    }

    public ArticleResponse createArticleResponse(String currUserUsername, Article article){

        User author = article.getAuthor();
        ProfileResponse authorProfile = (ProfileResponse) userService.getProfile(author.getUsername(), currUserUsername);
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
            Optional<Article> articleExists = articleRepository.findArticleBySlug(articleSlug);

            if(!articleExists.isPresent()) throw new ArticleNotFoundException("article with slug " + articleSlug + " was not found.");

            Article article = articleExists.get();

            User currUser = userRepository.findByUsername(currUserUsername).get();

            currUser.getFavouriteArticleList().add(article);
            article.getFavouriteByList().add(currUser);

            articleRepository.save(article);
            userRepository.save(currUser);

            return createArticleResponse(currUserUsername, article);


        }
        catch(ArticleNotFoundException e){

            return FailureResponse.builder()
                    .Exception(e)
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

            Optional<Article> articleExists = articleRepository.findArticleBySlug(articleSlug);

            if(!articleExists.isPresent()) throw new ArticleNotFoundException("article with slug " + articleSlug + " was not found.");

            Article article = articleExists.get();

            User currUser = userRepository.findByUsername(currUserUsername).get();

            if(!currUser.getFavouriteArticleList().contains(article) && !article.getFavouriteByList().contains(currUser)){

                return FailureResponse.builder()
                        .message("user has not favourited the article")
                        .status(HttpStatus.NOT_FOUND)
                        .build();
            }

            currUser.getFavouriteArticleList().remove(article);
            article.getFavouriteByList().remove(currUser);

            articleRepository.save(article);
            userRepository.save(currUser);

            return createArticleResponse(currUserUsername, article);


        }
        catch(ArticleNotFoundException e){

            return FailureResponse.builder()
                    .Exception(e)
                    .message(e.getMessage())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }
}
