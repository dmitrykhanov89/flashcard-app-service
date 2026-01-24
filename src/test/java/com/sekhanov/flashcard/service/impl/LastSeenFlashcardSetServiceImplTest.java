package com.sekhanov.flashcard.service.impl;

import com.sekhanov.flashcard.dto.LastSeenFlashcardSetDto;
import com.sekhanov.flashcard.dto.UserDTO;
import com.sekhanov.flashcard.entity.Cards;
import com.sekhanov.flashcard.entity.FlashcardSet;
import com.sekhanov.flashcard.entity.LastSeenFlashcardSet;
import com.sekhanov.flashcard.entity.LastSeenFlashcardSet.LastSeenFlashcardSetId;
import com.sekhanov.flashcard.entity.User;
import com.sekhanov.flashcard.repository.LastSeenFlashcardSetRepository;
import com.sekhanov.flashcard.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LastSeenFlashcardSetServiceImplTest {
    @Mock
    private LastSeenFlashcardSetRepository lastSeenRepo;
    @Mock
    private UserService userService;
    @InjectMocks
    private LastSeenFlashcardSetServiceImpl service;

    private final UserDTO currentUser = new UserDTO();

    @BeforeEach
    void setUp() {
        currentUser.setId(1L);
        currentUser.setName("Ivan");
        when(userService.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void saveLastSeenSet_whenCalled_shouldCallRepository() {
        service.saveLastSeenSet(10L);
        verify(lastSeenRepo).upsertLastSeenSet(currentUser.getId(), 10L);
    }

    @Test
    void getLastSeenSetsForCurrentUser_whenSetsExist_shouldReturnDtosInCorrectOrder() {
        LastSeenFlashcardSet lastSeen2 = makeLastSeen(2L, "Math", 0);
        LastSeenFlashcardSet lastSeen1 = makeLastSeen(1L, "English", 10);
        when(lastSeenRepo.findTop10ByUserIdOrderByOpenedAtDesc(currentUser.getId())).thenReturn(List.of(lastSeen2, lastSeen1));

        List<LastSeenFlashcardSetDto> result = service.getLastSeenSetsForCurrentUser();

        assertThat(result).extracting(LastSeenFlashcardSetDto::getFlashcardSetName).containsExactly("Math", "English");
        assertThat(result).extracting(LastSeenFlashcardSetDto::getFlashcardSetId).containsExactly(2L, 1L);
        verify(lastSeenRepo).findTop10ByUserIdOrderByOpenedAtDesc(currentUser.getId());
    }

    @Test
    void getLastSeenSetsForCurrentUser_whenNoSets_shouldReturnEmptyList() {
        when(lastSeenRepo.findTop10ByUserIdOrderByOpenedAtDesc(currentUser.getId())).thenReturn(List.of());

        assertThat(service.getLastSeenSetsForCurrentUser()).isEmpty();
        verify(lastSeenRepo).findTop10ByUserIdOrderByOpenedAtDesc(currentUser.getId());
    }

    // ---------------- фабрики ----------------
    private LastSeenFlashcardSet makeLastSeen(Long setId, String name, int minutesAgo) {
        FlashcardSet set = new FlashcardSet();
        set.setId(setId);
        set.setName(name);
        set.setOwner(TestEntities.owner());
        set.setCards(List.of(TestEntities.card(set)));

        LastSeenFlashcardSet lastSeen = new LastSeenFlashcardSet();
        lastSeen.setId(new LastSeenFlashcardSetId(currentUser.getId(), setId));
        lastSeen.setFlashcardSet(set);
        lastSeen.setOpenedAt(LocalDateTime.now().minusMinutes(minutesAgo));
        return lastSeen;
    }

    private static class TestEntities {
        private static User owner() {
            User owner = new User();
            owner.setName("Owner");
            owner.setSurname("Test");
            return owner;
        }

        private static Cards card(FlashcardSet set) {
            Cards card = new Cards();
            card.setTerm("term");
            card.setDefinition("definition");
            card.setFlashcardSet(set);
            return card;
        }
    }
}
