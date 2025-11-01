package com.sekhanov.flashcard.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Date;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {
    @InjectMocks
    private JwtServiceImpl jwtService;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        setSecretAndInit();
        token = jwtService.generateToken("ivan");
    }

    private void setSecretAndInit() throws Exception {
        String secret = "01234567890123456789012345678901";
        var secretField = JwtServiceImpl.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtService, secret);
        jwtService.init(); // инициализация ключа
    }

    @Test
    void generateToken_whenCalled_shouldReturnNonEmptyToken() {
        assertThat(token).isNotBlank();
    }

    @Test
    void extractLogin_whenValidToken_shouldReturnCorrectLogin() {
        assertThat(jwtService.extractLogin(token)).isEqualTo("ivan");
    }

    @Test
    void validateToken_whenTokenIsValid_shouldReturnTrue() {
        assertThat(jwtService.validateToken(token)).isTrue();
    }

    @Test
    void validateToken_whenTokenIsInvalid_shouldReturnFalse() {
        assertThat(jwtService.validateToken(token + "invalid")).isFalse();
    }

    @Test
    void refreshToken_whenCalled_shouldReturnValidTokenWithSameLogin() {
        String newToken = jwtService.refreshToken(token);
        assertThat(newToken).isNotBlank();
        assertThat(jwtService.extractLogin(newToken)).isEqualTo("ivan");
        assertThat(jwtService.extractExpiration(newToken)).isAfter(new Date());
    }

    @Test
    void extractExpiration_whenCalled_shouldReturnFutureDate() {
        assertThat(jwtService.extractExpiration(token)).isAfter(new Date());
    }
}
