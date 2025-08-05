package com.sekhanov.flashcard.service;

import com.sekhanov.flashcard.dto.LastSeenFlashcardSetDto;
import java.util.List;

/**
 * Сервис для управления недавними наборами карточек, с которыми взаимодействовал пользователь.
 */
public interface LastSeenFlashcardSetService {
    /**
     * Сохраняет набор карточек как недавно использованный пользователем.
     * Если запись уже существует, она может быть обновлена с новой временной меткой.
     *
     * @param flashcardSetId идентификатор набора карточек
     */
    void saveLastSeenSet(Long flashcardSetId);

    /**
     * Возвращает список последних использованных пользователем наборов карточек.
     *
     * @return список DTO недавних наборов карточек
     */
    List<LastSeenFlashcardSetDto> getLastSeenSetsForCurrentUser();
}
