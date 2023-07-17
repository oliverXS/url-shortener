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
    private static final String REGEX = "^[a-zA-Z0-9_-]+$";
    private static final int CUSTOM_URL_MIN_LEN = 4;
    private static final int CUSTOM_URL_MAX_LEN = 20;

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

    public String customizeUrl(String longUrl, String customPath) {
        if (!isValidCustomPath(customPath)) {
            throw  new IllegalArgumentException("Invalid custom path.");
        }

        String redisKey = REDIS_KEY_PREFIX + customPath;
        // Check if customPath is already in use
        if (redisTemplate.hasKey(redisKey)) {
            throw new IllegalArgumentException("Custom path is already in use.");
        }
        redisTemplate.opsForValue().set(redisKey, longUrl);
        return customPath;
    }

    private boolean isValidCustomPath(String customPath) {
        // Custom URL length must be between 4 and 20 characters
        if (customPath.length() < CUSTOM_URL_MIN_LEN || customPath.length() > CUSTOM_URL_MAX_LEN) {
            return false;
        }

        // Custom URL must only contain alphanumeric characters, "-", and "_"
        if (!customPath.matches(REGEX)) {
            return false;
        }

        return true;
    }

    private String generateShortUrl() {
        SnowFlake snowFlake = new SnowFlake(1);
        long id = snowFlake.generateId();

        return convertBase10ToBase58(id);
    }
}
