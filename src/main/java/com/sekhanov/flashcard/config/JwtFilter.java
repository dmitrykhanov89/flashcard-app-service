package com.sekhanov.flashcard.config;

import com.sekhanov.flashcard.service.JwtService;
import com.sekhanov.flashcard.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

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
            }
        }
        chain.doFilter(req, res);
    }
}
