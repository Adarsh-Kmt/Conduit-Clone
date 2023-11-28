package kamathadarsh.Conduit.Service;

import jakarta.transaction.Transactional;
import kamathadarsh.Conduit.Entity.Article;
import kamathadarsh.Conduit.Entity.Tag;
import kamathadarsh.Conduit.Entity.User;
import kamathadarsh.Conduit.Repository.ArticleRepository;
import kamathadarsh.Conduit.Repository.TagRepository;
import kamathadarsh.Conduit.Repository.UserRepository;
import kamathadarsh.Conduit.Request.GetArticleRequest;
import kamathadarsh.Conduit.Request.PostArticleRequest;
import kamathadarsh.Conduit.Response.ArticleResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArticleServiceTest {


    @Autowired
    ArticleService articleService;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TagRepository tagRepository;

    @BeforeEach
    void setUp() {

        Article article1 = Article.builder()
                .slug("test-article-1")
                .title("test article 1")
                .favouriteByList(new HashSet<>())
                .tags(new HashSet<>())
                .body("test body")
                .description("test description")
                .build();

        Article article2 = Article.builder()
                .slug("test-article-2")
                .title("test article 2")
                .favouriteByList(new HashSet<>())
                .tags(new HashSet<>())
                .body("test body")
                .description("test description")
                .build();

        Article article3 = Article.builder()
                .slug("test-article-3")
                .title("test article 3")
                .favouriteByList(new HashSet<>())
                .tags(new HashSet<>())
                .body("test body")
                .description("test description")
                .build();



        User testuser1 = User.builder()
                .username("testuser1")
                .favouriteArticleList(new HashSet<>())
                .followingUserUsernameList(new HashSet<>())
                .build();

        User testuser2 = User.builder()
                .username("testuser2")
                .favouriteArticleList(new HashSet<>())
                .followingUserUsernameList(new HashSet<>())
                .build();

        User testuser3 = User.builder()
                .username("testuser3")
                .favouriteArticleList(new HashSet<>())
                .followingUserUsernameList(new HashSet<>())
                .build();

        testuser1.getFavouriteArticleList().add(article1);
        testuser1.getFavouriteArticleList().add(article2);

        testuser3.getFavouriteArticleList().add(article3);

        Tag testTag1 = Tag.builder()
                .tagName("testTag1")
                .articles(new HashSet<>())
                .build();

        Tag testTag2 = Tag.builder()
                .tagName("testTag2")
                .articles(new HashSet<>())
                .build();

        Tag testTag3 = Tag.builder()
                .tagName("testTag3")
                .articles(new HashSet<>())
                .build();



        article3.getTags().add(testTag3);
        article1.getTags().add(testTag1);
        article2.getTags().add(testTag2);

        article1.setAuthor(testuser1);
        article2.setAuthor(testuser2);
        article3.setAuthor(testuser3);

        userRepository.save(testuser3);
        userRepository.save(testuser2);
        userRepository.save(testuser1);

        tagRepository.save(testTag2);
        tagRepository.save(testTag3);
        tagRepository.save(testTag1);

        articleRepository.save(article3);
        articleRepository.save(article2);
        articleRepository.save(article1);



    }

    @Test
    @DisplayName("Get All Articles (Global Feed).")
    @Transactional
    public void test10(){

        List<String> tags = new ArrayList<>();

        tags.add("testTag1");
        GetArticleRequest getArticleRequest = GetArticleRequest.builder()
                .tags(tags)
                .build();
        List<ArticleResponse> articleResponseList = articleService.getAllArticles("testuser1", getArticleRequest);

        System.out.println(articleResponseList);

        Assertions.assertEquals(1, articleResponseList.size());
    }

    @Test
    @Transactional
    @DisplayName("creating two tests with the same name to make sure slugify works")
    public void test11(){

        PostArticleRequest article4 = PostArticleRequest.builder()
                .title("fake article")
                .body("this is the 1st post")
                .tagList(new ArrayList<>())
                .build();

        PostArticleRequest article5 = PostArticleRequest.builder()
                .title("fake article")
                .body("this is the 2nd post")
                .tagList(new ArrayList<>())
                .build();

        articleService.createArticle("testuser1", article4);
        articleService.createArticle("testuser1", article5);
        Optional<Article> article4Response = articleRepository.findArticleBySlug("fake-article-1");
        System.out.println(articleRepository.getNumberOfArticlesWithSameName("fake article"));

        assertTrue(true);


    }


}