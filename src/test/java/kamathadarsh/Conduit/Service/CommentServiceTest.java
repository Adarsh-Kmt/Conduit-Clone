package kamathadarsh.Conduit.Service;

import jakarta.transaction.Transactional;
import kamathadarsh.Conduit.Entity.Article;
import kamathadarsh.Conduit.Entity.Comment;
import kamathadarsh.Conduit.Entity.User;
import kamathadarsh.Conduit.Repository.ArticleRepository;
import kamathadarsh.Conduit.Repository.CommentRepository;
import kamathadarsh.Conduit.Repository.UserRepository;
import kamathadarsh.Conduit.Request.CommentRequest;
import kamathadarsh.Conduit.Response.CommentResponse;
import kamathadarsh.Conduit.Response.MultipleCommentResponse;
import kamathadarsh.Conduit.Response.SingleCommentResponse;
import kamathadarsh.Conduit.Response.SuccessResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {

        User user1 = User.builder()
                .followingUserUsernameList(new HashSet<>())
                .username("user1")
                .build();

        User user2 = User.builder()
                .followingUserUsernameList(new HashSet<>())
                .username("user2")
                .build();

        User user3 = User.builder()
                .followingUserUsernameList(new HashSet<>())
                .username("user3")
                .build();

        Article article = Article.builder()
                .favouriteByList(new HashSet<>())
                .slug("article-new")
                .build();

        Comment comment1 = Comment.builder()
                .user(user1)
                .body("1st comment.")
                .build();

        Comment comment2 = Comment.builder()
                .user(user2)
                .body("2nd comment.")
                .build();

        Comment comment3 = Comment.builder()
                .user(user3)
                .body("3rd comment.")
                .build();

        comment1.setArticle(article);
        comment2.setArticle(article);
        comment3.setArticle(article);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        articleRepository.save(article);

        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);


    }

    @Test
    @Transactional
    @DisplayName("get all comments under an article.")
    void getAllCommentsUnderAnArticle() {


        MultipleCommentResponse response = (MultipleCommentResponse) commentService.getAllCommentsUnderAnArticle("user1", "article-new");

        List<CommentResponse> commentResponseList= response.getComments();


        System.out.println(commentResponseList);

        Assertions.assertEquals(3, commentResponseList.size());



    }

    @Test
    @Transactional
    @DisplayName("post a comment under an article.")
    void postComment() {

        User user4 = User.builder()
                .followingUserUsernameList(new HashSet<>())
                .username("user4")
                .build();
        CommentRequest commentRequest = new CommentRequest("this is a comment");
        Article article5 = Article.builder()
                .slug("article-new-2")
                .favouriteByList(new HashSet<>())
                .build();

        userRepository.save(user4);
        articleRepository.save(article5);

        SingleCommentResponse response = (SingleCommentResponse) commentService.postComment("user4", "article-new-2", commentRequest);

        System.out.println(response);

        assertTrue(true);
    }
}