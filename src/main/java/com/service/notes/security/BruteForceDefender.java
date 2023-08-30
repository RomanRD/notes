package com.service.notes.security;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.service.notes.domain.exception.BruteForceException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class BruteForceDefender {

    public final int MAX_ATTEMPTS;
    public final int BLOCKING_HOURS;

    private final HttpServletRequest request;
    private final LoadingCache<String, Integer> attemptsCache; //thread safe

    private static final Logger logger = LoggerFactory.getLogger(BruteForceDefender.class);

    public BruteForceDefender(@Value("${security.brute-force.max-attempts}") Integer maxAttempts,
                              @Value("${security.brute-force.blocking-hours}") Integer blockingHours,
                              HttpServletRequest request) {
        this.request = request;
        this.MAX_ATTEMPTS = maxAttempts;
        this.BLOCKING_HOURS = blockingHours;
        this.attemptsCache = CacheBuilder.newBuilder()
                .expireAfterWrite(BLOCKING_HOURS, TimeUnit.HOURS)
                .build(new CacheLoader<>() {
                    @Override
                    public Integer load(@NonNull final String key) {
                        return 0;
                    }
                });
    }

    public void loginFailed() {
        try {
            String key = getClientIP();
            int attempts = attemptsCache.get(key);
            attemptsCache.put(key, attempts + 1);
        } catch (ExecutionException e) {
            logger.error("Exception while recording a failed login", e);
        }
    }

    public void checkCurrentClientIPBlocked() throws BruteForceException {
        if(isCurrentClientIPBlocked()){
            throw new BruteForceException(BLOCKING_HOURS);
        }
    }

    public boolean isCurrentClientIPBlocked() {
        try {
            return attemptsCache.get(getClientIP()) >= MAX_ATTEMPTS;
        } catch (ExecutionException e) {
            logger.error("Exception while checking current client ip blocking", e);
            return false;
        }
    }

    private String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
            return request.getRemoteAddr();
        } else {
            return xfHeader.split(",")[0];
        }
    }

}