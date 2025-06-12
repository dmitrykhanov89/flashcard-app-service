package com.sekhanov.flashcard.service;

import com.sekhanov.flashcard.dto.CreateWordListDTO;
import com.sekhanov.flashcard.dto.WordListDTO;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления списками слов (WordList).
 */
public interface WordListService {
    /**
     * Создает новый список слов.
     *
     * @param createWordListDTO DTO с данными для создания списка слов
     * @return DTO созданного списка слов
     */
    WordListDTO createWordList(CreateWordListDTO createWordListDTO);
    /**
     * Получает список слов по его id.
     *
     * @param id идентификатор списка слов
     * @return Optional с WordListDTO, если найден
     */
    Optional<WordListDTO> getWordListById(Long id);
    /**
     * Получает список слов по его названию.
     *
     * @param name название списка слов
     * @return Optional с WordListDTO, если найден
     */
    Optional<WordListDTO> getWordListByName(String name);
    /**
     * Получает все списки слов.
     *
     * @return список всех WordListDTO
     */
    List<WordListDTO> getAllWordLists();
    /**
     * Обновляет существующий список слов.
     *
     * @param id идентификатор обновляемого списка
     * @param updateDTO DTO с обновленными данными
     * @return Optional с обновленным WordListDTO, если найден
     */
    Optional<WordListDTO> updateWordList(Long id, CreateWordListDTO updateDTO);
    /**
     * Удаляет список слов по его идентификатору.
     *
     * @param id идентификатор удаляемого списка слов
     * @return true, если удаление успешно; false, если список не найден
     */
    boolean deleteWordList(Long id);
    /**
     * Добавляет список слов пользователю.
     *
     * @param userId      идентификатор пользователя
     * @param wordListId  идентификатор списка слов
     * @return true, если список был успешно добавлен; false, если пользователь или список не найдены
     */
    boolean addWordListToUser(Long userId, Long wordListId);
    /**
     * Удаляет список слов у пользователя.
     *
     * @param userId     идентификатор пользователя
     * @param wordListId идентификатор списка слов
     * @return true, если список был успешно удален; false, если пользователь или список не найдены
     */
    boolean removeWordListFromUser(Long userId, Long wordListId);
}
