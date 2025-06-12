package com.sekhanov.flashcard.config;

import com.sekhanov.flashcard.config.JwtFilter;
import com.sekhanov.flashcard.entity.User;
import com.sekhanov.flashcard.repository.UserRepository;
import com.sekhanov.flashcard.service.JwtService;
import com.sekhanov.flashcard.service.UserService;
import com.sekhanov.flashcard.service.impl.UserServiceImpl;
import lombok.Builder;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
                        .requestMatchers("/api/auth/**").permitAll()
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
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                User user = userRepository.findByLogin(username);
                if (user == null) {
                    throw new UsernameNotFoundException("User Not Found");
                } else {
                    return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassword(), List.of());
                }
            }
        };
    }
}
