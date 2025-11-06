package com.sekhanov.flashcard.service.impl;

import com.sekhanov.flashcard.dto.LastSeenFlashcardSetDto;
import com.sekhanov.flashcard.entity.LastSeenFlashcardSet;
import com.sekhanov.flashcard.repository.LastSeenFlashcardSetRepository;
import com.sekhanov.flashcard.service.LastSeenFlashcardSetService;
import com.sekhanov.flashcard.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для управления недавно просмотренными наборами карточек.
 * Сохраняет информацию о последних открытых наборах и предоставляет доступ к ним.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LastSeenFlashcardSetServiceImpl implements LastSeenFlashcardSetService {

    private final LastSeenFlashcardSetRepository lastSeenRepo;
    private final UserService userService;

    @Override
    @Transactional
    public void saveLastSeenSet(Long flashcardSetId) {
        Long userId = userService.getCurrentUser().getId();
        log.debug("Сохранение набора карточек id={} как последнего просмотренного для пользователя id={}", flashcardSetId, userId);
        lastSeenRepo.upsertLastSeenSet(userId, flashcardSetId);
        log.info("Последний просмотренный набор карточек id={} сохранён для пользователя id={}", flashcardSetId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LastSeenFlashcardSetDto> getLastSeenSetsForCurrentUser() {
        Long userId = userService.getCurrentUser().getId();
        log.debug("Получение последних просмотренных наборов карточек для пользователя id={}", userId);

        List<LastSeenFlashcardSetDto> result = lastSeenRepo.findTop10ByUserIdOrderByOpenedAtDesc(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        log.info("Найдено {} последних просмотренных наборов карточек для пользователя id={}", result.size(), userId);
        return result;
    }

    private LastSeenFlashcardSetDto toDto(LastSeenFlashcardSet entity) {
        log.trace("Преобразование LastSeenFlashcardSet(id={}, setId={}) в DTO",
                entity.getId(),
                entity.getFlashcardSet().getId());
        return new LastSeenFlashcardSetDto(
                entity.getFlashcardSet().getId(),
                entity.getFlashcardSet().getName(),
                entity.getOpenedAt()
        );
    }
}