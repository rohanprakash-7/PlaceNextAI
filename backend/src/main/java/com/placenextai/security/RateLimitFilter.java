package com.placenextai.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple fixed-window rate limiter for login/register endpoints, the one
 * class of request Spring Security's defaults don't protect against brute
 * force. In-memory and per-instance - fine for a single backend instance;
 * a horizontally scaled deployment would need a shared store (e.g. Redis)
 * instead.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_WINDOW = 10;
    private static final long WINDOW_MILLIS = Duration.ofMinutes(1).toMillis();

    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (!isRateLimited(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        String key = clientIp(request) + ":" + request.getRequestURI();
        Window window = windows.computeIfAbsent(key, ignored -> new Window());

        if (window.hasExpired()) {
            window.reset();
        }

        if (window.count.incrementAndGet() > MAX_REQUESTS_PER_WINDOW) {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    "{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Too many attempts - please wait a minute and try again.\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isRateLimited(String path) {
        return path.endsWith("/login") || path.endsWith("/register");
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static class Window {
        private final AtomicInteger count = new AtomicInteger(0);
        private volatile long windowStart = System.currentTimeMillis();

        boolean hasExpired() {
            return System.currentTimeMillis() - windowStart > WINDOW_MILLIS;
        }

        synchronized void reset() {
            if (hasExpired()) {
                count.set(0);
                windowStart = System.currentTimeMillis();
            }
        }
    }
}
