package com.sekhanov.flashcard.service.impl;

import com.sekhanov.flashcard.dto.LastSeenFlashcardSetDto;
import com.sekhanov.flashcard.entity.LastSeenFlashcardSet;
import com.sekhanov.flashcard.repository.LastSeenFlashcardSetRepository;
import com.sekhanov.flashcard.service.LastSeenFlashcardSetService;
import com.sekhanov.flashcard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для управления недавно просмотренными наборами карточек.
 * Сохраняет информацию о последних открытых наборах и предоставляет доступ к ним.
 */
@Service
@RequiredArgsConstructor
public class LastSeenFlashcardSetServiceImpl implements LastSeenFlashcardSetService {

    private final LastSeenFlashcardSetRepository lastSeenRepo;
    private final UserService userService;

    @Override
    @Transactional
    public void saveLastSeenSet(Long flashcardSetId) {
        lastSeenRepo.upsertLastSeenSet(userService.getCurrentUser().getId(), flashcardSetId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LastSeenFlashcardSetDto> getLastSeenSetsForCurrentUser() {
        Long userId = userService.getCurrentUser().getId();

        return lastSeenRepo.findTop10ByUserIdOrderByOpenedAtDesc(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private LastSeenFlashcardSetDto toDto(LastSeenFlashcardSet entity) {
        return new LastSeenFlashcardSetDto(
                entity.getFlashcardSet().getId(),
                entity.getFlashcardSet().getName(),
                entity.getOpenedAt()
        );
    }
}
