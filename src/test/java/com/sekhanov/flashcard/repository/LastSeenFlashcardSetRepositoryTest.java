package com.sekhanov.flashcard.repository;

import com.sekhanov.flashcard.entity.FlashcardSet;
import com.sekhanov.flashcard.entity.LastSeenFlashcardSet;
import com.sekhanov.flashcard.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LastSeenFlashcardSetRepositoryTest {
    @Autowired
    private LastSeenFlashcardSetRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FlashcardSetRepository flashcardSetRepository;
    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
        flashcardSetRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findTop10ByUserIdOrderByOpenedAtDesc_RecordsExist_ReturnsInDescendingOrder() {
        User user = new User(); user.setLogin("u"); user.setPassword("p"); user.setEmail("u@test"); userRepository.save(user);
        FlashcardSet set1 = new FlashcardSet(); set1.setName("1"); set1.setOwner(user);
        FlashcardSet set2 = new FlashcardSet(); set2.setName("2"); set2.setOwner(user);
        flashcardSetRepository.saveAll(List.of(set1, set2));

        LastSeenFlashcardSet l1 = new LastSeenFlashcardSet(); l1.setId(new LastSeenFlashcardSet.LastSeenFlashcardSetId(user.getId(), set1.getId()));
        l1.setUser(user); l1.setFlashcardSet(set1); l1.setOpenedAt(LocalDateTime.now());
        LastSeenFlashcardSet l2 = new LastSeenFlashcardSet(); l2.setId(new LastSeenFlashcardSet.LastSeenFlashcardSetId(user.getId(), set2.getId()));
        l2.setUser(user); l2.setFlashcardSet(set2); l2.setOpenedAt(LocalDateTime.now().plusSeconds(1));
        repository.saveAll(List.of(l1, l2));

        List<LastSeenFlashcardSet> results = repository.findTop10ByUserIdOrderByOpenedAtDesc(user.getId());
        assertEquals(2, results.size());
        assertEquals(set2.getId(), results.get(0).getFlashcardSet().getId());
        assertEquals(set1.getId(), results.get(1).getFlashcardSet().getId());
    }

    @Test
    void findTop10ByUserIdOrderByOpenedAtDesc_NoRecords_ReturnsEmptyList() {
        assertTrue(repository.findTop10ByUserIdOrderByOpenedAtDesc(999L).isEmpty());
    }

    @Test
    void saveLastSeen_NewRecord_InsertsRecord() {
        User user = new User(); user.setLogin("u"); user.setPassword("p"); user.setEmail("u@test"); userRepository.save(user);
        FlashcardSet set = new FlashcardSet(); set.setName("Set"); set.setOwner(user); flashcardSetRepository.save(set);

        LastSeenFlashcardSet lastSeen = new LastSeenFlashcardSet();
        lastSeen.setId(new LastSeenFlashcardSet.LastSeenFlashcardSetId(user.getId(), set.getId()));
        lastSeen.setUser(user); lastSeen.setFlashcardSet(set); lastSeen.setOpenedAt(LocalDateTime.now());
        repository.save(lastSeen);

        List<LastSeenFlashcardSet> results = repository.findTop10ByUserIdOrderByOpenedAtDesc(user.getId());
        assertEquals(1, results.size());
        assertEquals(set.getId(), results.get(0).getFlashcardSet().getId());
    }

    @Test
    void saveLastSeen_RecordExists_UpdatesOpenedAt() {
        User user = new User(); user.setLogin("u"); user.setPassword("p"); user.setEmail("u@test"); userRepository.save(user);
        FlashcardSet set = new FlashcardSet(); set.setName("Set"); set.setOwner(user); flashcardSetRepository.save(set);

        LastSeenFlashcardSet lastSeen = new LastSeenFlashcardSet();
        lastSeen.setId(new LastSeenFlashcardSet.LastSeenFlashcardSetId(user.getId(), set.getId()));
        lastSeen.setUser(user); lastSeen.setFlashcardSet(set); lastSeen.setOpenedAt(LocalDateTime.now());
        repository.save(lastSeen);

        LocalDateTime oldTime = lastSeen.getOpenedAt();
        lastSeen.setOpenedAt(LocalDateTime.now().plusSeconds(5));
        repository.save(lastSeen);

        LastSeenFlashcardSet updated = repository.findTop10ByUserIdOrderByOpenedAtDesc(user.getId()).get(0);
        assertEquals(set.getId(), updated.getFlashcardSet().getId());
        assertTrue(updated.getOpenedAt().isAfter(oldTime));
    }
}
