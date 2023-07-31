package com.oliver.urlshortener.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oliver.urlshortener.document.UrlMapping;
import com.oliver.urlshortener.service.UrlShortenerService;
import com.oliver.utils.SnowFlake;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;

/**
 * @author xiaorui
 */
public class UrlShortenerServiceImpl implements UrlShortenerService {
    private final MongoTemplate mongoTemplate;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String COLLECTION_NAME = "urlMappings";
    private static final String REDIS_KEY_PREFIX = "url:";
    private static final String BASE58_CHARS = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    private static final String REGEX = "^[a-zA-Z0-9_-]+$";
    private static final int CUSTOM_URL_MIN_LEN = 4;
    private static final int CUSTOM_URL_MAX_LEN = 20;

    public UrlShortenerServiceImpl(MongoTemplate mongoTemplate, RedisTemplate<String, String> redisTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public synchronized String shortenUrl(String longUrl) {
//        if (!isValidUrl(longUrl)) {
//            throw new IllegalArgumentException("Invalid URL.");
//        }
        // Generate short URL
        String shortUrl = generateShortUrl();

        // Add short url to db
        UrlMapping urlMapping = new UrlMapping(shortUrl, longUrl);
        mongoTemplate.save(urlMapping, COLLECTION_NAME);

        // Add short url to cache
        String redisKey = REDIS_KEY_PREFIX + shortUrl;
        redisTemplate.opsForValue().set(redisKey, longUrl);

        return shortUrl;
    }

    @Override
    public String getLongUrl(String shortUrl) {
        String redisKey = REDIS_KEY_PREFIX + shortUrl;
        // Check if the long URL is available in the cache
        Instant startCache = Instant.now();
        String longUrlJsonInCache = redisTemplate.opsForValue().get(redisKey);
        Instant endCache = Instant.now();

        if (longUrlJsonInCache != null) {
            String longUrlJson = longUrlJsonInCache;
            String longUrl = getUrlFromJson(longUrlJson);
            Duration duration = Duration.between(startCache, endCache);
            System.out.println("Get from cache!");
            System.out.println("Time taken from the cache:" + duration.toMillis() + " milliseconds");
            return longUrl;
        }

        // If not available in the cache, fetch it from the database
        Instant startDB = Instant.now();
        UrlMapping urlMapping = mongoTemplate.findOne(
                Query.query(Criteria.where("shortUrl").is(shortUrl)),
                UrlMapping.class,
                COLLECTION_NAME
        );
        Instant endDB = Instant.now();

        if (urlMapping != null) {
            String longUrlJson = urlMapping.getLongUrl();
            String longUrl = getUrlFromJson(longUrlJson);
            Duration duration = Duration.between(startDB, endDB);
            System.out.println("Get from DB!");
            System.out.println("Time taken from the database:" + duration.toMillis() + " milliseconds");
            return longUrl;
        }
        return null;
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

    @Override
    public String customizeUrl(String longUrl, String customPath) {
        if (!isValidCustomPath(customPath)) {
            throw new IllegalArgumentException("Invalid custom path.");
        }

        if (!isValidUrl(longUrl)) {
            throw new IllegalArgumentException("Invalid URL.");
        }

        UrlMapping existingMapping = mongoTemplate.findById(customPath, UrlMapping.class, COLLECTION_NAME);
        if (existingMapping != null) {
            throw new IllegalArgumentException("Custom path is already in use.");
        }

        UrlMapping urlMapping = new UrlMapping(customPath, longUrl);
        mongoTemplate.save(urlMapping, COLLECTION_NAME);
        return customPath;
    }

    private String getUrlFromJson(String urlJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(urlJson);
            return jsonNode.get("longUrl").asText();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isValidUrl(String url) {
        try {
            // Check syntax
            new URL(url).toURI();

            // Check accessibility
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            int statusCode = connection.getResponseCode();
            return statusCode == HttpURLConnection.HTTP_OK;
        } catch (MalformedURLException | URISyntaxException e) {
            // Syntax error
            return false;
        } catch (IOException e) {
            // Accessibility error
            return false;
        }
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
