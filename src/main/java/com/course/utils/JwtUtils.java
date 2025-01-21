package com.course.utils;

import com.course.exceptions.ServiceException;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Duration;

@ApplicationScoped
public class JwtUtils {

    @Inject
    JWTParser jwtParser;

    // Метод генерации токена
    public static String generateToken(String username, String role) {
        return Jwt.issuer("http://localhost:8080")
                .upn(username)
                .claim("role", role)
                .expiresIn(Duration.ofHours(1))
                .sign();
    }

    // Метод для проверки валидности токена
    public boolean isTokenValid(String token) {
        try {
            jwtParser.parse(token); // Если токен неверный, вызовет исключение
            return true;
        } catch (ParseException e) { // Используем исключение JWTException
            return false; // Токен невалиден
        }
    }

    public String getRoleFromToken(String token) {
        try {
            return jwtParser.parse(token).getClaim("role");
        } catch (ParseException e) {
            throw new ServiceException("Failed to extract role from token", e);
        }
    }
}