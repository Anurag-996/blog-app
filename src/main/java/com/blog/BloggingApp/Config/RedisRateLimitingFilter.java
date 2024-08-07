package com.blog.BloggingApp.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RedisRateLimitingFilter extends OncePerRequestFilter {

    private final RedisRateLimiter redisRateLimiter;

    public RedisRateLimitingFilter(RedisRateLimiter redisRateLimiter) {
        this.redisRateLimiter = redisRateLimiter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String clientIp = request.getRemoteAddr(); // Use IP or another unique identifier

        if (!redisRateLimiter.isAllowed(clientIp)) {
            response.setStatus(429); // HTTP status code 429: Too Many Requests
            response.getWriter().write("Too many requests");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
