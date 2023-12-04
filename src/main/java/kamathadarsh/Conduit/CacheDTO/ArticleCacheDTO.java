package kamathadarsh.Conduit.CacheDTO;

import kamathadarsh.Conduit.Entity.Article;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCacheDTO {

    private String slug;

    private String title;

    private String description;

    private String body;

    private Instant createdAt;

    private Instant updatedAt;

    private Integer favouriteCount;

    private ProfileCacheDTO authorProfile;

    public ArticleCacheDTO(Article article){

        this.slug = article.getSlug();
        this.title = article.getTitle();
        this.description = article.getDescription();
        this.updatedAt = article.getUpdatedAt();
        this.favouriteCount = article.getFavouriteCount();
        this.authorProfile = new ProfileCacheDTO(article.getAuthor());
    }
}
