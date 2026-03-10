package com.calendar.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * 缓存配置
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String USER_CACHE = "users";
    public static final String EVENT_CACHE = "events";
    public static final String SYNC_STATUS_CACHE = "sync_status";

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
            new ConcurrentMapCache(USER_CACHE),
            new ConcurrentMapCache(EVENT_CACHE),
            new ConcurrentMapCache(SYNC_STATUS_CACHE)
        ));
        return cacheManager;
    }
}
