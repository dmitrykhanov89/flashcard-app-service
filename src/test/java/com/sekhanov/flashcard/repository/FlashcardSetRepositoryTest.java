package com.sekhanov.flashcard.repository;

import com.sekhanov.flashcard.entity.FlashcardSet;
import com.sekhanov.flashcard.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FlashcardSetRepositoryTest {
    @Autowired
    private FlashcardSetRepository flashcardSetRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        flashcardSetRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByName_SetExists_ReturnsFlashcardSet() {
        FlashcardSet set = new FlashcardSet(); set.setName("English Basics");
        flashcardSetRepository.save(set);

        FlashcardSet found = flashcardSetRepository.findByName("English Basics");
        assertNotNull(found); assertEquals("English Basics", found.getName());
    }

    @Test
    void findByName_SetDoesNotExist_ReturnsNull() {
        assertNull(flashcardSetRepository.findByName("NonExistingName"));
    }

    @Test
    void findByOwnerId_SetsExistForOwner_ReturnsList() {
        User owner1 = persistUser("owner1");
        User owner2 = persistUser("owner2");

        List<FlashcardSet> sets = List.of(
                new FlashcardSet(null, "English Basics", null, false, owner1, null, null, null),
                new FlashcardSet(null, "French Basics", null, false, owner1, null, null, null),
                new FlashcardSet(null, "Spanish Basics", null, false, owner2, null, null, null));
        flashcardSetRepository.saveAll(sets);

        List<FlashcardSet> owner1Sets = flashcardSetRepository.findByOwnerId(owner1.getId());
        assertEquals(2, owner1Sets.size());
        List<String> names1 = owner1Sets.stream().map(FlashcardSet::getName).toList();
        assertTrue(names1.containsAll(List.of("English Basics", "French Basics")));

        List<FlashcardSet> owner2Sets = flashcardSetRepository.findByOwnerId(owner2.getId());
        assertEquals(1, owner2Sets.size());
        assertEquals("Spanish Basics", owner2Sets.get(0).getName());
    }

    @Test
    void findByOwnerId_NoSetsForOwner_ReturnsEmptyList() {
        List<FlashcardSet> sets = flashcardSetRepository.findByOwnerId(999L);
        assertNotNull(sets); assertTrue(sets.isEmpty());
    }

    private User persistUser(String suffix) {
        User user = new User();
        user.setLogin("login_" + suffix);
        user.setEmail(suffix + "@example.com");
        user.setPassword("password");
        return userRepository.save(user);
    }
}
