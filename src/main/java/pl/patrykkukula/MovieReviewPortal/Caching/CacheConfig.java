package pl.patrykkukula.MovieReviewPortal.Caching;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    @Bean
    @Primary
    public CacheManager cacheManager(){
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(){
            @Override
            protected Cache createCaffeineCache(String name){
                Caffeine<Object, Object> builder = getCaffeineBuilder(name);
                com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = builder.build();
                return new LoggingCaffeineCache(name, nativeCache);
            }
        };
        registerSpecificCaches(cacheManager);
        return cacheManager;
    }
    private Caffeine<Object, Object> caffeineCacheBuilder(){
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(500)
                .expireAfterWrite(Duration.ofMinutes(30))
                .recordStats()
                .removalListener(this::onCacheRemoval);
    }
    private Caffeine<Object, Object> getCaffeineBuilder(String cacheName){
        switch (cacheName){
            case "movie-details", "actor-details", "director-details" ->
            {
                return Caffeine.newBuilder()
                        .initialCapacity(200)
                        .maximumSize(1000)
                        .expireAfterWrite(Duration.ofHours(2))
                        .recordStats()
                        .removalListener(this::onCacheRemoval);
            }
            case "movie", "actor", "director", "user", "movies-dto", "topic" -> {
                return Caffeine.newBuilder()
                        .initialCapacity(30)
                        .maximumSize(100)
                        .expireAfterWrite(Duration.ofMinutes(30))
                        .recordStats()
                        .removalListener(this::onCacheRemoval);
            }
            case "top-rated-movies", "top-rated-actors", "top-rated-directors", "latest-topics" ->
            {
                return Caffeine.newBuilder()
                        .initialCapacity(5)
                        .maximumSize(5)
                        .expireAfterWrite(Duration.ofHours(4))
//                        .refreshAfterWrite(Duration.ofMinutes(30))
                        .recordStats()
                        .removalListener(this::onCacheRemoval);
            }
            case "all-actors", "all-movies", "all-directors", "all-movies-view", "all-actors-summary", "all-directors-summary"
                    , "all-entity-topics", "topics-by-title" -> {
                return Caffeine.newBuilder()
                        .initialCapacity(100)
                        .maximumSize(500)
                        .expireAfterWrite(Duration.ofMinutes(30))
                        .recordStats()
                        .removalListener(this::onCacheRemoval);
            }
            default -> {
                return caffeineCacheBuilder();
            }
        }
    }
    private void registerSpecificCaches(CaffeineCacheManager cacheManager){
        cacheManager.getCache("top-rated-movies");
        cacheManager.getCache("top-rated-actors");
        cacheManager.getCache("top-rated-directors");
        cacheManager.getCache("latest-topics");
    }
    private void onCacheRemoval(Object key, Object value, RemovalCause cause){
        log.info("Cache removal - Key: {}, Cause: {}", key, cause);
    }
}
