package kamathadarsh.Conduit.Service;

import kamathadarsh.Conduit.Entity.Article;
import kamathadarsh.Conduit.Entity.Tag;
import kamathadarsh.Conduit.Repository.ArticleRepository;
import kamathadarsh.Conduit.Repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TagServiceTest {

    @Autowired
    TagService tagService;

    @Autowired
    TagRepository tagRepository;

    @MockBean
    ArticleRepository articleRepository;

    @BeforeEach
    void setUp() {

        Article article1 = Article.builder()
                .slug("article-1")
                .build();

        Article article2 = Article.builder()
                .slug("article-2")
                .build();

        Article article3 = Article.builder()
                .slug("article-3")
                .build();

        Mockito.when(articleRepository.findArticleBySlug("article-1")).thenReturn(Optional.ofNullable(article1));
        Mockito.when(articleRepository.findArticleBySlug("article-2")).thenReturn(Optional.ofNullable(article2));
        Mockito.when(articleRepository.findArticleBySlug("article-3")).thenReturn(Optional.ofNullable(article3));
    }

    @Test
    @DisplayName("adding article to tag list.")
    public void test1(){


        Article article1 = articleRepository.findArticleBySlug("article-1").get();
        Article article2 = articleRepository.findArticleBySlug("article-2").get();
        Article article3 = articleRepository.findArticleBySlug("article-3").get();



        Tag newTag = Tag.builder()
                .tagName("test tag")
                .articles(new HashSet<>())
                .build();

        articleRepository.save(article3);
        articleRepository.save(article2);
        articleRepository.save(article1);
        tagRepository.save(newTag);

        newTag = tagService.addArticleToList(newTag, article1);
        newTag = tagService.addArticleToList(newTag, article2);
        newTag = tagService.addArticleToList(newTag, article3);




        assertTrue(newTag.getArticles().contains(article1));
        assertTrue(newTag.getArticles().contains(article2));
        assertTrue(newTag.getArticles().contains(article3));



    }
}