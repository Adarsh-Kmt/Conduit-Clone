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


//    public String slugify(String input) {
//
//        String slug = input.trim().toLowerCase().replace(" ", "-");
//
//        //----------------------------------------------------------------
//        // finding the number of articles with the same title, to create an appropriate title
//
//        int numberOfArticlesWithSameTitle = articleRepository
//                .getNumberOfArticlesWithSameName(input);;
//
//        if(numberOfArticlesWithSameTitle != 0){
//            String finalSlug = slug + "-" + numberOfArticlesWithSameTitle;
//            return finalSlug;
//        }
//
//        //----------------------------------------------------------------
//
//        return slug;
//    }

    public String slugify2(String articleName){

        String slug = articleName.trim().toLowerCase().replace(" ", "-");

        Optional<String> preExistingSlugsExists = articleRepository.getSlugOfLastCreatedArticleWithSameName(articleName);

        if(!preExistingSlugsExists.isPresent()) return slug;

        String preExistingSlug = preExistingSlugsExists.get();
        System.out.println("last existing slug is: " + preExistingSlug);
        String indexNumString = preExistingSlug.replace(slug, "");
        if(indexNumString.isBlank()) return slug + "-1";
        String indexNumString2 = indexNumString.replace("-", "");
        System.out.println("indexNumString is: " + indexNumString);
        int indexNum = Integer.parseInt(indexNumString2);
        System.out.println("index num is: " + indexNum);
        String newSlug = slug + "-" + (indexNum+1);

        return newSlug;
    }
    final ArticleRepository articleRepository;

    final UserService userService;

    final EntityManager entityManager;

    final UserRepository userRepository;

    final TagService tagService;

    final TagRepository tagRepository;
    public List<ArticleResponse> getAllArticles(String currUserUsername, GetArticleRequest getArticleRequest){

        //------------------------------------------------------------------------------------------------------
        // using Criteria to get Articles according to articleRequest

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Article> criteriaQuery = cb.createQuery(Article.class);

        Root<Article> articleRoot = criteriaQuery.from(Article.class);

        Join<Article, Tag> articleTagJoin = articleRoot.join("tags", JoinType.INNER);

        if(getArticleRequest.getTags() != null && !getArticleRequest.getTags().isEmpty()){
            Predicate tagsPredicate = articleTagJoin.get("tagName").in(getArticleRequest.getTags());

            criteriaQuery.where(tagsPredicate);
        }


        Join<Article, User> favouriteArticleUserJoin = articleRoot.join("favouriteByList", JoinType.INNER);

        if(getArticleRequest.getIsFavourited() != null && getArticleRequest.getIsFavourited() == true){

            criteriaQuery.where(cb.equal(favouriteArticleUserJoin.get("username"), currUserUsername));
        }

        Join<Article, User> ArticleAuthorUserJoin = articleRoot.join("author", JoinType.INNER);
        if(getArticleRequest.getAuthorUsername() != null && !getArticleRequest.getAuthorUsername().isBlank()){

            criteriaQuery.where(cb.equal(ArticleAuthorUserJoin.get("username"), getArticleRequest.getAuthorUsername()));
        }


        TypedQuery<Article> articleTypedQuery = entityManager.createQuery(criteriaQuery);

        int limit = 20;
        int offset = 0;

        // applying offset, if requested
        if(getArticleRequest.getOffset() != null) offset = getArticleRequest.getOffset();

        // applying limit, if requested.
        if(getArticleRequest.getLimit() != null) limit = getArticleRequest.getLimit();

        articleTypedQuery.setMaxResults(limit);
        articleTypedQuery.setFirstResult(offset);

        List<Article> finalArticleList = articleTypedQuery.getResultList();

        //-------------------------------------------------------------------------------------------------------
        // converting articles to articleResponses.
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
            Optional<Article> articleExists = articleRepository.findArticleBySlug(articleSlug);

            if(!articleExists.isPresent()) throw new ArticleNotFoundException("article with slug " + articleSlug + " was not found.");

            Article article = articleExists.get();
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
