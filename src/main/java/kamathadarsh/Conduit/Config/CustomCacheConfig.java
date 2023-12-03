package kamathadarsh.Conduit.Config;


import lombok.Builder;
import lombok.Data;
import org.redisson.spring.cache.CacheConfig;

@Builder
@Data

public class CustomCacheConfig extends CacheConfig {
    public CustomCacheConfig() {
        super();
    }

    public CustomCacheConfig(long ttl, long maxIdleTime) {
        super(ttl, maxIdleTime);
    }

    @Override
    public long getTTL() {
        return super.getTTL();
    }

    @Override
    public void setTTL(long ttl) {
        super.setTTL(ttl);
    }

    @Override
    public int getMaxSize() {
        return super.getMaxSize();
    }

    @Override
    public void setMaxSize(int maxSize) {
        super.setMaxSize(maxSize);
    }

    @Override
    public long getMaxIdleTime() {
        return super.getMaxIdleTime();
    }

    @Override
    public void setMaxIdleTime(long maxIdleTime) {
        super.setMaxIdleTime(maxIdleTime);
    }
}
