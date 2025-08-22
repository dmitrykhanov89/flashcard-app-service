package com.sekhanov.flashcard.repository;

import com.sekhanov.flashcard.entity.LastSeenFlashcardSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Репозиторий для работы с сущностью {@link LastSeenFlashcardSet}.
 * Предоставляет методы для получения и обновления информации
 * о последних просмотренных пользователем наборах карточек.
 */
public interface LastSeenFlashcardSetRepository extends JpaRepository<LastSeenFlashcardSet, Long> {
    /**
     * Возвращает 10 последних просмотренных пользователем наборов карточек,
     * отсортированных по времени открытия (по убыванию).
     *
     * @param userId идентификатор пользователя
     * @return список последних просмотренных наборов
     */
    List<LastSeenFlashcardSet> findTop10ByUserIdOrderByOpenedAtDesc(Long userId);

    /**
     * Добавляет или обновляет запись о просмотре набора карточек пользователем.
     * Если такая пара (user_id, flashcard_set_id) уже существует, то обновляется поле opened_at.
     *
     * @param userId         идентификатор пользователя
     * @param flashcardSetId идентификатор набора карточек
     */
    @Modifying
    @Transactional
    @Query(value = """
    INSERT INTO last_seen_flashcard_set (user_id, flashcard_set_id, opened_at)
    VALUES (:userId, :flashcardSetId, CURRENT_TIMESTAMP)
    ON CONFLICT (user_id, flashcard_set_id)
    DO UPDATE SET opened_at = EXCLUDED.opened_at
""", nativeQuery = true)
    void upsertLastSeenSet(@Param("userId") Long userId, @Param("flashcardSetId") Long flashcardSetId);
}
