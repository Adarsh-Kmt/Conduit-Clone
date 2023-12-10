package kamathadarsh.Conduit.Service;

import kamathadarsh.Conduit.CacheDTO.ArticleCacheDTO;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.Article;
import kamathadarsh.Conduit.Response.ArticleResponse;
import kamathadarsh.Conduit.Response.CustomResponse;
import kamathadarsh.Conduit.Response.FailureResponse;
import kamathadarsh.Conduit.Response.ProfileResponse;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.UserTable;
import kamathadarsh.Conduit.jooqRepository.JOOQUserRepository;
import lombok.AllArgsConstructor;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CacheService {

    final RedissonClient redissonClient;

    final JOOQUserRepository jooqUserRepository;

    public void addArticleToCache(Article article){

        RMap<String, ArticleCacheDTO> articleHash = redissonClient.getMap("article:");

        UserTable author = jooqUserRepository.findByUsername(article.getAuthorUsername()).get();
        List<String> followerUsernameList = jooqUserRepository.listOfFollowersUsername(article.getAuthorUsername());
        articleHash.put(article.getSlug(), new ArticleCacheDTO(article, author, followerUsernameList));
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
