package com.calendar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 缓存服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    /**
     * 缓存用户信息
     */
    @Cacheable(value = CacheConfig.USER_CACHE, key = "#userId")
    public Optional<Object> getCachedUser(Long userId) {
        log.debug("Cache miss for user: {}", userId);
        return Optional.empty();
    }

    /**
     * 更新用户缓存
     */
    @CachePut(value = CacheConfig.USER_CACHE, key = "#userId")
    public Object updateUserCache(Long userId, Object user) {
        log.debug("Updating cache for user: {}", userId);
        return user;
    }

    /**
     * 清除用户缓存
     */
    @CacheEvict(value = CacheConfig.USER_CACHE, key = "#userId")
    public void evictUserCache(Long userId) {
        log.debug("Evicting cache for user: {}", userId);
    }

    /**
     * 缓存事件
     */
    @Cacheable(value = CacheConfig.EVENT_CACHE, key = "#eventId")
    public Optional<Object> getCachedEvent(Long eventId) {
        log.debug("Cache miss for event: {}", eventId);
        return Optional.empty();
    }

    /**
     * 更新事件缓存
     */
    @CachePut(value = CacheConfig.EVENT_CACHE, key = "#eventId")
    public Object updateEventCache(Long eventId, Object event) {
        log.debug("Updating cache for event: {}", eventId);
        return event;
    }

    /**
     * 清除事件缓存
     */
    @CacheEvict(value = CacheConfig.EVENT_CACHE, key = "#eventId")
    public void evictEventCache(Long eventId) {
        log.debug("Evicting cache for event: {}", eventId);
    }

    /**
     * 清除用户所有事件缓存
     */
    @CacheEvict(value = CacheConfig.EVENT_CACHE, allEntries = true)
    public void evictAllEventCache() {
        log.debug("Evicting all event cache");
    }
}
