package kamathadarsh.Conduit.Repository;


import kamathadarsh.Conduit.Entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, String> {

    @Query(value = "select * from article where slug = :slug", nativeQuery = true)
    public Optional<Article> findArticleBySlug(String slug);

    @Query(value = "select count(*) from article where title = :articleName", nativeQuery = true)
    public Integer getNumberOfArticlesWithSameName(String articleName);

    @Query(value = "select slug from article where title = :articleName order by created_at desc LIMIT 1", nativeQuery = true)
    public Optional<String> getSlugOfLastCreatedArticleWithSameName(String articleName);
}
