package com.sekhanov.flashcard.service;

import com.sekhanov.flashcard.dto.CreateCardsDTO;
import com.sekhanov.flashcard.dto.CardsDTO;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления словами (Cards), связанными со списками слов (FlashcardSet).
 */
public interface CardsService {
    /**
     * Создает новое слово и привязывает его к существующему списку слов.
     *
     * @param wordListId ID списка слов, к которому добавляется новое слово
     * @param entryDTO   DTO с данными для создания слова
     * @return DTO созданного слова
     */
    CardsDTO createCards(Long wordListId, CreateCardsDTO entryDTO);
    /**
     * Получает слово по его идентификатору.
     *
     * @param id ID слова
     * @return Optional с DTO слова, если найдено
     */
    Optional<CardsDTO> getCardsById(Long id);
    /**
     * Получает все слова, принадлежащие указанному списку слов.
     *
     * @param wordListId ID списка слов
     * @return список DTO слов, принадлежащих списку
     */
    List<CardsDTO> getAllCardsForFlashcardSet(Long wordListId);
    /**
     * Обновляет существующее слово по его идентификатору.
     *
     * @param id       ID слова для обновления
     * @param entryDTO DTO с новыми значениями
     * @return Optional с обновленным DTO слова, если найдено
     */
    Optional<CardsDTO> updateCards(Long id, CreateCardsDTO entryDTO);
    /**
     * Удаляет слово по его идентификатору.
     *
     * @param id ID удаляемого слова
     * @return true, если удаление прошло успешно; false, если слово не найдено
     */
    boolean deleteCards(Long id);
}
