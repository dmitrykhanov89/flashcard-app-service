package com.sekhanov.flashcard.service;

import com.sekhanov.flashcard.dto.CreateFlashcardSetDTO;
import com.sekhanov.flashcard.dto.FlashcardSetDTO;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления списками слов (FlashcardSet).
 */
public interface FlashcardSetService {
    /**
     * Создает новый список слов.
     *
     * @param createFlashcardSetDTO DTO с данными для создания списка слов
     * @return DTO созданного списка слов
     */
    FlashcardSetDTO createFlashcardSet(CreateFlashcardSetDTO createFlashcardSetDTO);
    /**
     * Получает список слов по его id.
     *
     * @param id идентификатор списка слов
     * @return Optional с FlashcardSetDTO, если найден
     */
    Optional<FlashcardSetDTO> getFlashcardSetById(Long id);
    /**
     * Получает список слов по его названию.
     *
     * @param name название списка слов
     * @return Optional с FlashcardSetDTO, если найден
     */
    Optional<FlashcardSetDTO> getFlashcardSetByName(String name);
    /**
     * Получает все списки слов.
     *
     * @return список всех FlashcardSetDTO
     */
    List<FlashcardSetDTO> getAllFlashcardSet();
    /**
     * Обновляет существующий список слов.
     *
     * @param id идентификатор обновляемого списка
     * @param updateDTO DTO с обновленными данными
     * @return Optional с обновленным FlashcardSetDTO, если найден
     */
    Optional<FlashcardSetDTO> updateFlashcardSet(Long id, CreateFlashcardSetDTO updateDTO);
    /**
     * Удаляет список слов по его идентификатору.
     *
     * @param id идентификатор удаляемого списка слов
     * @return true, если удаление успешно; false, если список не найден
     */
    boolean deleteFlashcardSet(Long id);
    /**
     * Добавляет список слов пользователю.
     *
     * @param userId      идентификатор пользователя
     * @param wordListId  идентификатор списка слов
     * @return true, если список был успешно добавлен; false, если пользователь или список не найдены
     */
    boolean addFlashcardSetToUser(Long userId, Long wordListId);
    /**
     * Удаляет список слов у пользователя.
     *
     * @param userId     идентификатор пользователя
     * @param wordListId идентификатор списка слов
     * @return true, если список был успешно удален; false, если пользователь или список не найдены
     */
    boolean removeFlashcardSetFromUser(Long userId, Long wordListId);

    /**
     * Получает список наборов карточек, принадлежащих конкретному пользователю (владельцу).
     *
     * @param ownerId Идентификатор пользователя-владельца наборов карточек.
     * @return Список {@link FlashcardSetDTO} наборов карточек, принадлежащих пользователю.
     */
    List<FlashcardSetDTO> getFlashcardSetsByOwnerId(Long ownerId);
}
