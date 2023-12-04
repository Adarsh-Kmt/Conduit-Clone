package kamathadarsh.Conduit.Repository;


import kamathadarsh.Conduit.Entity.Article;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.Optional;

@CacheConfig(cacheNames = {"articleCache"})
public interface ArticleRepository extends JpaRepository<Article, String> {


    @Query(value = "select * from article where slug = :slug", nativeQuery = true)
    public Optional<Article> findArticleBySlug(String slug);


}
