package pl.patrykkukula.MovieReviewPortal.Caching;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class CacheLoggingInterceptor {
    @Autowired
    private CacheManager cacheManager;

    @Around("@annotation(cacheable)")
    public Object interceptCacheable(ProceedingJoinPoint jointPoint, Cacheable cacheable) throws Throwable{
        String cacheName = cacheable.value().length > 0 ? cacheable.value()[0] : "default";
        Object[] args = jointPoint.getArgs();

        String key = generateKey(jointPoint.getSignature().getName(), args);

        long startTime = System.currentTimeMillis();

        CacheManager cacheManager = getCacheManager();
        Cache cache = cacheManager.getCache(cacheName);
        boolean wasInCache = cache != null && cache.get(key) != null;

        Object result = jointPoint.proceed();

        long duration = System.currentTimeMillis() - startTime ;

        if (!wasInCache) log.info("[CACHE MISS] - Cache:{} , Key:{} , Time: {}ms", cacheName, key, duration);

        return result;
    }

    private String generateKey(String methodName, Object[] args){
        return methodName + "_" + Arrays.toString(args);
    }
    private CacheManager getCacheManager(){
        return cacheManager;
    }
}
