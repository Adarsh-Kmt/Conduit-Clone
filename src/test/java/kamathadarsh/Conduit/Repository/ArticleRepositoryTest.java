package kamathadarsh.Conduit.Repository;

import kamathadarsh.Conduit.Entity.Article;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
class ArticleRepositoryTest {

    @Autowired
    ArticleRepository articleRepository;

    @Test
    @DisplayName("get article by slug name")
    public void successfulArticleRetrieval(){

        Article article = Article.builder()
                .slug("insert-slug-here")
                .build();

        articleRepository.save(article);

        Optional<Article> articleExists = articleRepository.findArticleBySlug("insert-slug-here");

        assertTrue(articleExists.isPresent());
        Assertions.assertEquals("insert-slug-here", articleExists.get().getSlug());

    }
}