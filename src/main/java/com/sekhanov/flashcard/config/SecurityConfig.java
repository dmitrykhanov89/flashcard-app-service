package com.sekhanov.flashcard.config;

import com.sekhanov.flashcard.entity.User;
import com.sekhanov.flashcard.repository.UserRepository;
import com.sekhanov.flashcard.service.JwtService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.util.Collection;
import java.util.List;

/**
 * Конфигурация безопасности Spring Security.
 * <p>
 * Настраивает цепочку фильтров безопасности, правила доступа и управление сессиями.
 * Включает JWT-фильтр для обработки аутентификации по JWT токену.
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    // Пути, которые должны быть доступны без аутентификации для Swagger
    private static final String[] WHITE_LIST_URLS = {
            "/api/auth/**",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    /**
     * Конфигурирует {@link SecurityFilterChain}, определяющую правила безопасности HTTP-запросов.
     * <ul>
     *     <li>Отключает CSRF-защиту (так как используется stateless с JWT)</li>
     *     <li>Включает CORS с настройками по умолчанию</li>
     *     <li>Устанавливает политику управления сессиями как stateless (без сессий)</li>
     *     <li>Разрешает все запросы к путям /api/auth/** без аутентификации</li>
     *     <li>Требует аутентификацию для всех остальных запросов</li>
     *     <li>Добавляет фильтр JWT перед фильтром UsernamePasswordAuthenticationFilter</li>
     * </ul>
     *
     * @param http объект для конфигурации HTTP безопасности
     * @return настроенная цепочка фильтров безопасности
     * @throws Exception в случае ошибок конфигурации безопасности
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {})
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITE_LIST_URLS).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Создает бин {@link AuthenticationManager}, используемый для аутентификации пользователей.
     *
     * @param config конфигурация аутентификации Spring Security
     * @return объект AuthenticationManager
     * @throws Exception если не удалось получить AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(userDetailsService());
        return authenticationProvider;
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtService, userDetailsService());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByLogin(username);
            if (user == null) {
                throw new UsernameNotFoundException("User Not Found");
            }
            return new CustomUserDetails(user.getId(), user.getLogin(), user.getName(), user.getSurname(), user.getPassword(), List.of());
        };
    }

    /**
     * Представляет пользователя с дополнительной информацией: id, имя и фамилия.
     * <p>
     * Используется для аутентификации и авторизации в системе.
     */
    @Getter
    public static class CustomUserDetails extends org.springframework.security.core.userdetails.User {
        private final Long id;
        private final String name;
        private final String surname;

        /**
         * Создаёт объект пользователя с заданными данными и полномочиями.
         */
        public CustomUserDetails(Long id, String login, String name, String surname, String password, Collection<? extends GrantedAuthority> authorities) {
            super(login, password, authorities);
            this.id = id;
            this.name = name;
            this.surname = surname;
        }
    }
}
