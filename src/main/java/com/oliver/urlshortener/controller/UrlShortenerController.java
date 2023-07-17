package com.oliver.urlshortener.controller;

import com.oliver.urlshortener.service.UrlShortenerService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author xiaorui
 */
@RestController
public class UrlShortenerController {
    private UrlShortenerService urlShortenerService;

    public UrlShortenerController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping("/shorten")
    public String shortenUrl(@RequestBody String longUrl) {
        String shortUrl = urlShortenerService.shortenUrl(longUrl);
        return shortUrl;
    }

    @PostMapping("/customize")
    public String customizeUrl(@RequestParam String longUrl, @RequestParam String customPath) {
        try {
            String customUrl = urlShortenerService.customizeUrl(longUrl, customPath);
            return customUrl;
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/{shortUrl}")
    public RedirectView redirectToLongUrl(@PathVariable String shortUrl) {
        String longUrl = urlShortenerService.getLongUrl(shortUrl);
        if (longUrl != null) {
            return new RedirectView(longUrl);
        } else {
            return new RedirectView("/error");
        }
    }
}
