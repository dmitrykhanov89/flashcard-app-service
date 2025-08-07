package com.sekhanov.flashcard.repository;

import com.sekhanov.flashcard.entity.FlashcardSet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Репозиторий для работы с сущностями {@link FlashcardSet}.
 * Расширяет {@link JpaRepository} для базовых операций CRUD.
 */
public interface FlashcardSetRepository extends JpaRepository<FlashcardSet, Long> {

    /**
     * Находит набор карточек по его уникальному имени.
     *
     * @param name имя набора карточек
     * @return объект {@link FlashcardSet} с заданным именем или null, если не найден
     */
    FlashcardSet findByName(String name);

    /**
     * Находит все наборы карточек, принадлежащие владельцу с указанным ID.
     *
     * @param ownerId идентификатор владельца (пользователя)
     * @return список наборов карточек {@link FlashcardSet}, принадлежащих владельцу
     */
    List<FlashcardSet> findByOwnerId(Long ownerId);
}
