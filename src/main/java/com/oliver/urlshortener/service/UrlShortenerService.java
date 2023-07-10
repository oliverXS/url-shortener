package com.oliver.urlshortener.service;

import com.oliver.utils.SnowFlake;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.beans.PropertyEditorSupport;

/**
 * @author xiaorui
 */
@Service
public class UrlShortenerService {
    private RedisTemplate<String, String> redisTemplate;
    private final String REDIS_KEY_PREFIX = "url:";
    private static final String BASE58_CHARS = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

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

    public static String convertBase10ToBase58(long id) {
        StringBuilder sb = new StringBuilder();

        while (id > 0) {
            int remainder = (int) (id % 58);
            sb.insert(0, BASE58_CHARS.charAt(remainder));
            id /= 58;
        }

        return sb.toString();
    }

    private String generateShortUrl() {
        SnowFlake snowFlake = new SnowFlake(1);
        long id = snowFlake.generateId();

        return convertBase10ToBase58(id);
    }
}
