package com.sekhanov.flashcard.repository;

import com.sekhanov.flashcard.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void findByLogin_UserExists_ReturnsUser() {
        User u = new User(); u.setLogin("u"); u.setEmail("u@test"); u.setPassword("p"); u.setIsEmailConfirmed(true);
        userRepository.save(u);

        User found = userRepository.findByLogin("u");
        assertNotNull(found); assertEquals("u", found.getLogin());
    }

    @Test
    void findByLogin_UserDoesNotExist_ReturnsNull() {
        assertNull(userRepository.findByLogin("nonexistent"));
    }

    @Test
    void findByConfirmationToken_TokenExists_ReturnsUser() {
        User u = new User(); u.setLogin("u"); u.setEmail("u@test"); u.setPassword("p"); u.setConfirmationToken("token");
        userRepository.save(u);

        Optional<User> found = userRepository.findByConfirmationToken("token");
        assertTrue(found.isPresent()); assertEquals("token", found.get().getConfirmationToken());
    }

    @Test
    void findByConfirmationToken_TokenDoesNotExist_ReturnsEmpty() {
        assertTrue(userRepository.findByConfirmationToken("invalid").isEmpty());
    }

    @Test
    void findAllByIsEmailConfirmedTrue_SomeUsersConfirmed_ReturnsConfirmedUsers() {
        User u1 = new User(); u1.setLogin("u1"); u1.setEmail("u1@test"); u1.setPassword("p"); u1.setIsEmailConfirmed(true);
        User u2 = new User(); u2.setLogin("u2"); u2.setEmail("u2@test"); u2.setPassword("p"); u2.setIsEmailConfirmed(false);
        User u3 = new User(); u3.setLogin("u3"); u3.setEmail("u3@test"); u3.setPassword("p"); u3.setIsEmailConfirmed(true);
        userRepository.saveAll(List.of(u1,u2,u3));

        List<User> confirmed = userRepository.findAllByIsEmailConfirmedTrue();
        assertEquals(2, confirmed.size());
        List<String> logins = confirmed.stream().map(User::getLogin).toList();
        assertTrue(logins.containsAll(List.of("u1","u3")));
    }

    @Test
    void findAllByIsEmailConfirmedTrue_NoUsersConfirmed_ReturnsEmptyList() {
        User u = new User(); u.setLogin("u"); u.setEmail("u@test"); u.setPassword("p"); u.setIsEmailConfirmed(false);
        userRepository.save(u);

        List<User> confirmed = userRepository.findAllByIsEmailConfirmedTrue();
        assertNotNull(confirmed); assertTrue(confirmed.isEmpty());
    }
}
