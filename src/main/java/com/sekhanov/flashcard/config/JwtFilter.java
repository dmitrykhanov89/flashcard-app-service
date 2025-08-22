package com.sekhanov.flashcard.config;

import com.sekhanov.flashcard.service.JwtService;
import com.sekhanov.flashcard.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;

/**
 * Фильтр для проверки JWT токена в заголовке Authorization HTTP-запроса.
 *
 * <p>Если токен валиден, фильтр извлекает имя пользователя из токена,
 * загружает пользователя через {@link UserService} и устанавливает
 * аутентификацию в контексте безопасности Spring Security.</p>
 *
 * <p>Данный фильтр выполняется один раз на каждый запрос.</p>
 */
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private static final long REFRESH_THRESHOLD = Duration.ofMinutes(15).toMillis();

    /**
     * Выполняет фильтрацию входящего HTTP-запроса.
     *
     * <p>Извлекает JWT токен из заголовка Authorization, проверяет его валидность,
     * и если валиден — устанавливает аутентификацию пользователя в контексте безопасности.</p>
     *
     * @param req   HTTP-запрос
     * @param res   HTTP-ответ
     * @param chain цепочка фильтров
     * @throws ServletException в случае ошибки сервлета
     * @throws IOException      в случае ошибки ввода-вывода
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String h = req.getHeader("Authorization");
        if (h != null && h.startsWith("Bearer ")) {
            String token = h.substring(7);
            if (jwtService.validateToken(token)) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(jwtService.extractLogin(token));
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                Date expiration = jwtService.extractExpiration(token);
                Date now = new Date();
                if (expiration.getTime() - now.getTime() <= REFRESH_THRESHOLD) {
                    String refreshedToken = jwtService.refreshToken(token);
                    res.setHeader("Authorization", "Bearer " + refreshedToken);
                }
            }
        }
        chain.doFilter(req, res);
    }
}
