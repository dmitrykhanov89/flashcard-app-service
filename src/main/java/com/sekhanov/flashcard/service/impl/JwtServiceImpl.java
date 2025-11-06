package com.sekhanov.flashcard.service.impl;

import com.sekhanov.flashcard.service.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secret;
    private Key key;
    private final long expiration = Duration.ofHours(1).toMillis(); // 1 час

    @PostConstruct
    public void init() {
        log.debug("Инициализация JwtServiceImpl с секретом длиной {} символов", secret.length());
        key = Keys.hmacShaKeyFor(secret.getBytes());
        log.info("JWT ключ успешно инициализирован");
    }

    @Override
    public String generateToken(String login) {
        log.debug("Генерация JWT токена для login={}", login);
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);
        String token = Jwts.builder()
                .setSubject(login)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)
                .compact();
        log.info("JWT токен успешно создан для login={}", login);
        return token;
    }

    @Override
    public String extractLogin(String token) {
        log.debug("Извлечение логина из токена");
        String login = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        log.trace("Из токена извлечён login={}", login);
        return login;
    }

    @Override
    public boolean validateToken(String token) {
        log.debug("Проверка валидности JWT токена");
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            log.trace("JWT токен действителен");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT токен недействителен: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String refreshToken(String token) {
        log.debug("Обновление JWT токена");
        String login = extractLogin(token);
        String newToken = generateToken(login);
        log.info("JWT токен успешно обновлён для login={}", login);
        return newToken;
    }

    @Override
    public Date extractExpiration(String token) {
        log.debug("Извлечение времени истечения из токена");
        Date expirationDate = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        log.trace("Токен истекает {}", expirationDate);
        return expirationDate;
    }
}