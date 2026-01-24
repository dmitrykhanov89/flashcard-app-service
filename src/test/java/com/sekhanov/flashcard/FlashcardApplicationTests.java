package com.sekhanov.flashcard;

import com.sekhanov.flashcard.service.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
class FlashcardApplicationTests {

    @TestConfiguration
    static class MailServiceTestConfig {
        @Bean
        MailService mailService() {
            return (emailAddress, subject, message) -> { };
        }
    }

    @Test
    void contextLoads() {
    }

}
