package kamathadarsh.Conduit.Service;

import kamathadarsh.Conduit.CacheDTO.ArticleCacheDTO;
import kamathadarsh.Conduit.Entity.Article;
import kamathadarsh.Conduit.Response.ArticleResponse;
import kamathadarsh.Conduit.Response.CustomResponse;
import kamathadarsh.Conduit.Response.FailureResponse;
import kamathadarsh.Conduit.Response.ProfileResponse;
import lombok.AllArgsConstructor;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CacheService {

    final RedissonClient redissonClient;

    public void addArticleToCache(Article article){



        RMap<String, ArticleCacheDTO> articleHash = redissonClient.getMap("article:");

        articleHash.put(article.getSlug(), new ArticleCacheDTO(article));
    }

    public CustomResponse getArticleFromCacheIfAvailable(String currUserUsername, String articleSlug){

        RMap<String, ArticleCacheDTO> articleHash = redissonClient.getMap("article:");
        boolean articleExists = articleHash.containsKey(articleSlug);

        if(articleExists){
            return createArticleResponse(currUserUsername, articleHash.get(articleSlug));
        }

        return FailureResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .build();
    }

    public ArticleResponse createArticleResponse(String currUserUsername, ArticleCacheDTO articleCacheDTO){

        ProfileResponse authorProfile = articleCacheDTO.getAuthorProfile().convertToProfileResponse(currUserUsername);

        return ArticleResponse.builder()
                .updatedAt(articleCacheDTO.getUpdatedAt())
                .createdAt(articleCacheDTO.getCreatedAt())
                .slug(articleCacheDTO.getSlug())
                .title(articleCacheDTO.getTitle())
                .body(articleCacheDTO.getBody())
                .description(articleCacheDTO.getDescription())
                .authorProfile(authorProfile)
                .favouriteCount(articleCacheDTO.getFavouriteCount())
                .build();
    }
}
