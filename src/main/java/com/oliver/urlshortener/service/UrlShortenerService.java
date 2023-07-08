package com.oliver.urlshortener.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author xiaorui
 */
@Service
public class UrlShortenerService {
    private RedisTemplate<String, String> redisTemplate;
    private final String REDIS_KEY_PREFIX = "url:";
    private final int SHORT_URL_LENGTH = 6;

    public UrlShortenerService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String shortenUrl(String longUrl) {
        String shortUrl = generateShortUrl();
        String redisKey = REDIS_KEY_PREFIX + shortUrl;
        redisTemplate.opsForValue().set(redisKey, longUrl);
        return shortUrl;
    }

    public String getLongUrl(String shortUrl) {
        String redisKey = REDIS_KEY_PREFIX + shortUrl;
        return redisTemplate.opsForValue().get(redisKey);
    }

    private String generateShortUrl() {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < SHORT_URL_LENGTH; i++) {
            int index = (int) (Math.random() * characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }
}
