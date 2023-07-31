package com.oliver.urlshortener.service;

import org.springframework.stereotype.Service;

/**
 * @author xiaorui
 */
@Service
public interface UrlShortenerService {
    /**
     * Generate short url from long url
     * @param longUrl
     * @return short url
     */
    String shortenUrl(String longUrl);

    /**
     * Get long url from short url
     * @param shortUrl
     * @return long url
     */
    String getLongUrl(String shortUrl);

    /**
     * Generate custom url
     * @param longUrl
     * @param customPath
     * @return custom url
     */
    String customizeUrl(String longUrl, String customPath);
}
