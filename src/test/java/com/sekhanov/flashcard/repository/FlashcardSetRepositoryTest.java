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
    @BeforeEach
    void cleanDatabase() {
        flashcardSetRepository.deleteAll();
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
        User o1 = new User(); o1.setId(1L);
        User o2 = new User(); o2.setId(2L);

        List<FlashcardSet> sets = List.of(
                new FlashcardSet(null, "English Basics", null, false, o1, null, null, null),
                new FlashcardSet(null, "French Basics", null, false, o1, null, null, null),
                new FlashcardSet(null, "Spanish Basics", null, false, o2, null, null, null));
        flashcardSetRepository.saveAll(sets);

        List<FlashcardSet> owner1Sets = flashcardSetRepository.findByOwnerId(1L);
        assertEquals(2, owner1Sets.size());
        List<String> names1 = owner1Sets.stream().map(FlashcardSet::getName).toList();
        assertTrue(names1.containsAll(List.of("English Basics", "French Basics")));

        List<FlashcardSet> owner2Sets = flashcardSetRepository.findByOwnerId(2L);
        assertEquals(1, owner2Sets.size());
        assertEquals("Spanish Basics", owner2Sets.getFirst().getName());
    }

    @Test
    void findByOwnerId_NoSetsForOwner_ReturnsEmptyList() {
        List<FlashcardSet> sets = flashcardSetRepository.findByOwnerId(999L);
        assertNotNull(sets); assertTrue(sets.isEmpty());
    }
}
