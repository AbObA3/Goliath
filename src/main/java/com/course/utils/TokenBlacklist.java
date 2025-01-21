package com.course.utils;

import com.course.exceptions.ServiceException;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class TokenBlacklist {

    private final ConcurrentHashMap<String, Long> blacklist = new ConcurrentHashMap<>();

    @Inject
    JWTParser jwtParser;  // Подключаем JWT парсер для работы с токенами

    @Inject
    Logger logger;

    public void addToken(String token) {
        try {
            long expirationTime = getTokenExpirationTime(token);
            blacklist.put(token, expirationTime); // Храним токен с временем истечения
        } catch (ParseException e) {
            logger.errorf("Failed to get exp time from token: %s", e.getMessage());
        }
    }

    public boolean isTokenRevoked(String token) {
        Long expirationTime = blacklist.get(token);
        return expirationTime != null && System.currentTimeMillis() < expirationTime;
    }

    @Scheduled(every = "1h")  // Выполняется каждые 1 час
    @RunOnVirtualThread
    public void clearExpiredTokens() {
        // Очистка устаревших токенов
        long currentTime = System.currentTimeMillis();
        blacklist.entrySet().removeIf(entry -> currentTime > entry.getValue());
    }

    private long getTokenExpirationTime(String token) throws ParseException {
        // Извлекаем payload токена и получаем значение времени истечения
        Long expClaim = jwtParser.parse(token).getClaim("exp");
        if (expClaim == null) {
            throw new ServiceException("Token does not have 'exp' claim");
        }
        // Преобразуем строку времени из claim в миллисекунды
        return expClaim * 1000; // В JWT время истечения (exp) представлено в секундах
    }
}