package com.sekhanov.flashcard.service.impl;

import com.sekhanov.flashcard.dto.CreateCardsDTO;
import com.sekhanov.flashcard.dto.CardsDTO;
import com.sekhanov.flashcard.entity.Cards;
import com.sekhanov.flashcard.entity.FlashcardSet;
import com.sekhanov.flashcard.repository.CardsRepository;
import com.sekhanov.flashcard.repository.FlashcardSetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardsServiceImplTest {
    @Mock
    private CardsRepository cardsRepository;
    @Mock
    private FlashcardSetRepository flashcardSetRepository;
    @InjectMocks
    private CardsServiceImpl cardsService;

    private FlashcardSet flashcardSet;

    @BeforeEach
    void setUp() {
        flashcardSet = new FlashcardSet();
        flashcardSet.setId(1L);
    }

    @Test
    void createCards_withValidFlashcardSet_shouldSaveAndReturnDTO() {
        when(flashcardSetRepository.findById(1L)).thenReturn(Optional.of(flashcardSet));
        when(cardsRepository.save(any(Cards.class))).thenReturn(makeCard(10L, "term", "definition"));

        CardsDTO result = cardsService.createCards(1L, new CreateCardsDTO("term", "definition"));

        assertThat(result).extracting(CardsDTO::getId, CardsDTO::getTerm, CardsDTO::getDefinition).containsExactly(10L, "term", "definition");
        verify(cardsRepository).save(any());
    }

    @Test
    void createCards_withNonExistingFlashcardSet_shouldThrowRuntimeException() {
        when(flashcardSetRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> cardsService.createCards(1L, new CreateCardsDTO("term", "definition")));
        verify(cardsRepository, never()).save(any());
    }

    @Test
    void getCardsById_whenCardExists_shouldReturnDTO() {
        when(cardsRepository.findById(5L)).thenReturn(Optional.of(makeCard(5L, "hello", "привет")));

        Optional<CardsDTO> result = cardsService.getCardsById(5L);

        assertThat(result).isPresent().get().extracting(CardsDTO::getTerm, CardsDTO::getDefinition).containsExactly("hello", "привет");
    }

    @Test
    void getCardsById_whenCardDoesNotExist_shouldReturnEmpty() {
        when(cardsRepository.findById(5L)).thenReturn(Optional.empty());

        assertThat(cardsService.getCardsById(5L)).isEmpty();
    }

    @Test
    void getAllCardsForFlashcardSet_withCards_shouldReturnListOfDTOs() {
        when(cardsRepository.findByFlashcardSetId(1L)).thenReturn(List.of(makeCard(11L, "t", "d")));

        List<CardsDTO> result = cardsService.getAllCardsForFlashcardSet(1L);

        assertThat(result).hasSize(1).first().extracting(CardsDTO::getTerm, CardsDTO::getDefinition).containsExactly("t", "d");
    }

    @Test
    void updateCards_whenCardExists_shouldUpdateAndReturnDTO() {
        when(cardsRepository.findById(20L)).thenReturn(Optional.of(makeCard(20L, "old", "oldDef")));
        when(cardsRepository.save(any(Cards.class))).thenReturn(makeCard(20L, "new", "newDef"));

        Optional<CardsDTO> result = cardsService.updateCards(20L, new CreateCardsDTO("new", "newDef"));

        assertThat(result).isPresent().get().extracting(CardsDTO::getTerm, CardsDTO::getDefinition).containsExactly("new", "newDef");
    }

    @Test
    void updateCards_whenCardDoesNotExist_shouldReturnEmpty() {
        when(cardsRepository.findById(20L)).thenReturn(Optional.empty());

        assertThat(cardsService.updateCards(20L, new CreateCardsDTO("x", "y"))).isEmpty();
        verify(cardsRepository, never()).save(any());
    }

    @Test
    void deleteCards_whenCardExists_shouldDeleteAndReturnTrue() {
        when(cardsRepository.existsById(30L)).thenReturn(true);

        assertThat(cardsService.deleteCards(30L)).isTrue();
        verify(cardsRepository).deleteById(30L);
    }

    @Test
    void deleteCards_whenCardDoesNotExist_shouldReturnFalse() {
        when(cardsRepository.existsById(30L)).thenReturn(false);

        assertThat(cardsService.deleteCards(30L)).isFalse();
        verify(cardsRepository, never()).deleteById(any());
    }

    // ---------- фабрики ----------
    private Cards makeCard(Long id, String term, String def) {
        Cards c = new Cards();
        c.setId(id);
        c.setTerm(term);
        c.setDefinition(def);
        c.setFlashcardSet(flashcardSet);
        return c;
    }
}
