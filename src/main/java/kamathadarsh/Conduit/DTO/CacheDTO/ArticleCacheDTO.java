package kamathadarsh.Conduit.DTO.CacheDTO;

import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.Article;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.UserTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCacheDTO {

    private String slug;

    private String title;

    private String description;

    private String body;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer favouriteCount;

    private ProfileCacheDTO authorProfile;

    public ArticleCacheDTO(Article article, UserTable author, List<String> followerUsernameList){

        this.slug = article.getSlug();
        this.title = article.getTitle();
        this.description = article.getDescription();
        this.updatedAt = article.getUpdatedAt();
        this.favouriteCount = article.getFavouriteCount();
        this.authorProfile = new ProfileCacheDTO(author, followerUsernameList);
    }
}
