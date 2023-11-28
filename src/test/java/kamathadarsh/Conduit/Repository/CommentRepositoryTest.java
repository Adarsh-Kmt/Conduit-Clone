package kamathadarsh.Conduit.Repository;

import jakarta.transaction.Transactional;
import kamathadarsh.Conduit.Entity.Article;
import kamathadarsh.Conduit.Entity.Comment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
class CommentRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;
    @Test
    @Transactional
    @DisplayName("getting all comments, comment with particular Id for an article.")
    public void getAllCommentsUnderAnArticleTest(){

        Article article = Article.builder()
                .slug("new-article")
                .build();

        Comment comment1 = Comment.builder()
                .body("1st comment")
                .article(article)
                .build();

        Comment comment2 = Comment.builder()
                .body("2nd comment")
                .article(article)
                .build();

        articleRepository.save(article);
        commentRepository.save(comment1);
        commentRepository.save(comment2);

        List<Comment> commentList = commentRepository.findAllCommentsUnderAnArticle("new-article");

        Assertions.assertEquals(2, commentList.size());
        Assertions.assertEquals("1st comment", commentList.get(0).getBody());
        Assertions.assertEquals("2nd comment", commentList.get(1).getBody());

        Optional<Comment> commentWithId = commentRepository.getCommentUnderAnArticleById("new-article", 1L);

        assertTrue(commentWithId.isPresent());
        Assertions.assertEquals("1st comment", commentWithId.get().getBody());


    }




}