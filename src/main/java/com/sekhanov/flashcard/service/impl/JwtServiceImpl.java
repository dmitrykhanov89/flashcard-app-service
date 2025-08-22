package com.sekhanov.flashcard.service.impl;

import com.sekhanov.flashcard.service.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.time.Duration;
import java.util.Date;

/**
 * Реализация сервиса {@link com.sekhanov.flashcard.service.JwtService},
 * отвечающего за генерацию, извлечение и валидацию JWT-токенов.
 * <p>
 * Использует симметричный ключ (HMAC-SHA256) для подписания токенов.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Создание JWT-токена с логином пользователя в качестве subject</li>
 *     <li>Извлечение логина из токена</li>
 *     <li>Проверка подлинности и срока действия токена</li>
 * </ul>
 */
@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    @PostConstruct
    public void init() {
        // Конвертируем секрет в ключ (ключ должен быть минимум 256 бит)
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }
    private final long expiration = Duration.ofHours(1).toMillis(); // 1 час

    @Override
    public String generateToken(String login) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .setSubject(login)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)
                .compact();
    }

    @Override
    public String extractLogin(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String refreshToken(String token) {
        String login = extractLogin(token);
        return generateToken(login);
    }

    @Override
    public Date extractExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
}
