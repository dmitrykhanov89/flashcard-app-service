package com.sekhanov.flashcard.repository;

import com.sekhanov.flashcard.entity.Cards;
import com.sekhanov.flashcard.entity.FlashcardSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CardsRepositoryTest {
    @Autowired
    private CardsRepository cardsRepository;
    @Autowired
    private FlashcardSetRepository flashcardSetRepository;
    @BeforeEach
    void cleanDatabase() {
        flashcardSetRepository.deleteAll();
    }

    @Test
    void findByFlashcardSetId_CardsExist_ReturnsCards() {
        FlashcardSet set = new FlashcardSet(); set.setName("English Basics");
        flashcardSetRepository.save(set);

        Cards c1 = new Cards(); c1.setFlashcardSet(set); c1.setTerm("Hello"); c1.setDefinition("Привет");
        Cards c2 = new Cards(); c2.setFlashcardSet(set); c2.setTerm("World"); c2.setDefinition("Мир");
        cardsRepository.saveAll(List.of(c1, c2));

        List<Cards> results = cardsRepository.findByFlashcardSetId(set.getId());
        assertEquals(2, results.size());
        List<String> terms = results.stream().map(Cards::getTerm).toList();
        assertTrue(terms.containsAll(List.of("Hello", "World")));
    }

    @Test
    void findByFlashcardSetId_NoCards_ReturnsEmptyList() {
        FlashcardSet set = new FlashcardSet(); set.setName("Empty Set");
        flashcardSetRepository.save(set);

        List<Cards> results = cardsRepository.findByFlashcardSetId(set.getId());
        assertNotNull(results); assertTrue(results.isEmpty());
    }
}

