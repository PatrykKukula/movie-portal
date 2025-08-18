package pl.patrykkukula.MovieReviewPortal.Caching;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.caffeine.CaffeineCache;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class LoggingCaffeineCache extends CaffeineCache {
    public LoggingCaffeineCache(String name, Cache<Object, Object> cache) {
        super(name, cache);
    }
    @Override
    public ValueWrapper get(Object key){
        ValueWrapper vw = super.get(key);
        log.info("[CACHE GET {}] key={} → {}", getName(), key, (vw != null ? "HIT" : "MISS"));
        return vw;
    }
    @Override
    public <T> T get(Object key, Callable<T> valueLoader){
        final AtomicBoolean loaded = new AtomicBoolean(false);
        T value;
        try {
            value = super.get(key, () -> {
                loaded.set(true);
                log.info("[CACHE GET {}] Loading value for key={}", getName(), key);
                return valueLoader.call();
            });
        }
        catch (Exception ex){
            log.error("[CACHE GET {}] Error loading key={}: {}", getName(), key, ex.getMessage());
            throw ex;
        }
        if (loaded.get()) {
            log.info("[CACHE GET {}] key={} → MISS → LOAD → PUT", getName(), key);
        } else {
            log.info("[CACHE GET {}] key={} → HIT", getName(), key);
        }
        return value;
    }
    @Override
    public void put(Object key, Object value) {
        log.info("[CACHE PUT {}] key={}", getName(), key);
        super.put(key, value);
    }
    @Override
    public void evict(Object key) {
        log.info("[CACHE EVICT{}] key={}", getName(), key);
        super.evict(key);
    }
}
