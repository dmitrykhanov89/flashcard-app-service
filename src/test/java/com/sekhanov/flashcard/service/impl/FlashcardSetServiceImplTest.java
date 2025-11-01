package com.sekhanov.flashcard.service.impl;

import com.sekhanov.flashcard.dto.CreateCardsDTO;
import com.sekhanov.flashcard.dto.CreateFlashcardSetDTO;
import com.sekhanov.flashcard.dto.FlashcardSetDTO;
import com.sekhanov.flashcard.entity.FlashcardSet;
import com.sekhanov.flashcard.entity.User;
import com.sekhanov.flashcard.repository.FlashcardSetRepository;
import com.sekhanov.flashcard.repository.UserRepository;
import com.sekhanov.flashcard.service.LastSeenFlashcardSetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlashcardSetServiceImplTest {
    @Mock
    private FlashcardSetRepository flashcardSetRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LastSeenFlashcardSetService lastSeenFlashcardSetService;
    @InjectMocks
    private FlashcardSetServiceImpl flashcardSetService;

    private User user;
    private FlashcardSet flashcardSet;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Ivan");

        flashcardSet = new FlashcardSet();
        flashcardSet.setId(10L);
        flashcardSet.setName("English");
        flashcardSet.setDescription("Basic words");
        flashcardSet.setOwner(user);
    }

    @Test
    void createFlashcardSet_withValidUser_shouldSaveAndReturnDTO() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(flashcardSetRepository.save(any())).thenReturn(flashcardSet);
        CreateFlashcardSetDTO dto = makeCreateFlashcardSetDTO(1L, "English", "Basic words",
                List.of(new CreateCardsDTO("hello", "привет")));

        FlashcardSetDTO result = flashcardSetService.createFlashcardSet(dto);

        assertThat(result).extracting(FlashcardSetDTO::getId, FlashcardSetDTO::getName).containsExactly(10L, "English");
        verify(userRepository).findById(1L);
        verify(flashcardSetRepository).save(any());
    }

    @Test
    void getFlashcardSetById_whenExists_shouldReturnDTO() {
        when(flashcardSetRepository.findById(10L)).thenReturn(Optional.of(flashcardSet));

        Optional<FlashcardSetDTO> result = flashcardSetService.getFlashcardSetById(10L);

        assertThat(result).isPresent().get().extracting(FlashcardSetDTO::getName).isEqualTo("English");
        verify(lastSeenFlashcardSetService).saveLastSeenSet(10L);
    }

    @Test
    void getFlashcardSetByName_whenExists_shouldReturnDTO() {
        when(flashcardSetRepository.findByName("English")).thenReturn(flashcardSet);

        assertThat(flashcardSetService.getFlashcardSetByName("English")).isPresent().get().extracting(FlashcardSetDTO::getDescription)
                .isEqualTo("Basic words");
    }

    @Test
    void getAllFlashcardSet_withExistingSets_shouldReturnListOfDTOs() {
        when(flashcardSetRepository.findAll()).thenReturn(List.of(flashcardSet));

        List<FlashcardSetDTO> result = flashcardSetService.getAllFlashcardSet();

        assertThat(result).hasSize(1).first().extracting(FlashcardSetDTO::getName).isEqualTo("English");
    }

    @Test
    void updateFlashcardSet_whenExists_shouldUpdateAndReturnDTO() {
        when(flashcardSetRepository.findById(10L)).thenReturn(Optional.of(flashcardSet));
        when(flashcardSetRepository.save(any())).thenReturn(flashcardSet);
        CreateFlashcardSetDTO dto = makeCreateFlashcardSetDTO(null, "Updated", "Updated description",
                List.of(new CreateCardsDTO("world", "мир")));

        Optional<FlashcardSetDTO> result = flashcardSetService.updateFlashcardSet(10L, dto);

        assertThat(result).isPresent().get().extracting(FlashcardSetDTO::getName).isEqualTo("Updated");
    }

    @Test
    void deleteFlashcardSet_whenExists_shouldReturnTrue() {
        when(flashcardSetRepository.existsById(10L)).thenReturn(true);

        assertThat(flashcardSetService.deleteFlashcardSet(10L)).isTrue();
        verify(flashcardSetRepository).deleteById(10L);
    }

    @Test
    void addFlashcardSetToUser_whenUserAndSetExist_shouldReturnTrue() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(flashcardSetRepository.findById(10L)).thenReturn(Optional.of(flashcardSet));

        assertThat(flashcardSetService.addFlashcardSetToUser(1L, 10L)).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    void removeFlashcardSetFromUser_whenSetExists_shouldRemoveAndReturnTrue() {
        user.getFlashcardSets().add(flashcardSet);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(flashcardSetRepository.findById(10L)).thenReturn(Optional.of(flashcardSet));

        assertThat(flashcardSetService.removeFlashcardSetFromUser(1L, 10L)).isTrue();
        assertThat(user.getFlashcardSets()).doesNotContain(flashcardSet);
        verify(userRepository).save(user);
    }

    @Test
    void getFlashcardSetsByOwnerId_withExistingSets_shouldReturnListOfDTOs() {
        when(flashcardSetRepository.findByOwnerId(1L)).thenReturn(List.of(flashcardSet));

        List<FlashcardSetDTO> result = flashcardSetService.getFlashcardSetsByOwnerId(1L);

        assertThat(result).hasSize(1).first().extracting(FlashcardSetDTO::getName).isEqualTo("English");
    }

    // ---------------- фабрики ----------------
    private CreateFlashcardSetDTO makeCreateFlashcardSetDTO(Long userId, String name, String description, List<CreateCardsDTO> cards) {
        CreateFlashcardSetDTO dto = new CreateFlashcardSetDTO();
        dto.setUserId(userId);
        dto.setName(name);
        dto.setDescription(description);
        dto.setCards(cards);
        return dto;
    }
}
