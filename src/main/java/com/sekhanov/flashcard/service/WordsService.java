package com.sekhanov.flashcard.service;

import com.sekhanov.flashcard.dto.CreateWordsDTO;
import com.sekhanov.flashcard.dto.WordsDTO;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления словами (Words), связанными со списками слов (WordList).
 */
public interface WordsService {
    /**
     * Создает новое слово и привязывает его к существующему списку слов.
     *
     * @param wordListId ID списка слов, к которому добавляется новое слово
     * @param entryDTO   DTO с данными для создания слова
     * @return DTO созданного слова
     */
    WordsDTO createWords(Long wordListId, CreateWordsDTO entryDTO);
    /**
     * Получает слово по его идентификатору.
     *
     * @param id ID слова
     * @return Optional с DTO слова, если найдено
     */
    Optional<WordsDTO> getWordsById(Long id);
    /**
     * Получает все слова, принадлежащие указанному списку слов.
     *
     * @param wordListId ID списка слов
     * @return список DTO слов, принадлежащих списку
     */
    List<WordsDTO> getAllWordsForWordList(Long wordListId);
    /**
     * Обновляет существующее слово по его идентификатору.
     *
     * @param id       ID слова для обновления
     * @param entryDTO DTO с новыми значениями
     * @return Optional с обновленным DTO слова, если найдено
     */
    Optional<WordsDTO> updateWords(Long id, CreateWordsDTO entryDTO);
    /**
     * Удаляет слово по его идентификатору.
     *
     * @param id ID удаляемого слова
     * @return true, если удаление прошло успешно; false, если слово не найдено
     */
    boolean deleteWords(Long id);
}
