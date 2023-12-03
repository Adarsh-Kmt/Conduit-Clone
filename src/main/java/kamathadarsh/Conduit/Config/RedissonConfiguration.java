package kamathadarsh.Conduit.Config;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableCaching
public class RedissonConfiguration {

    @Bean
    public RedissonClient redissonClient(){

        Config config = new Config();

        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379");



        return Redisson.create(config);

    }



    @Bean
    public RedissonSpringCacheManager cacheManager(RedissonClient redissonClient){

        Map<String, CustomCacheConfig> configMap = new HashMap<>();

        CustomCacheConfig config = new CustomCacheConfig(24*60*1000L, 12*60*1000L);
        config.setMaxSize(1000);

        configMap.put("articleCache", config);
        return new RedissonSpringCacheManager(redissonClient, configMap );
    }



}
