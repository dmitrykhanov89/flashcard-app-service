package com.sekhanov.flashcard.service;

import com.sekhanov.flashcard.dto.CreateUserDTO;
import com.sekhanov.flashcard.dto.UserDTO;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления пользователями (User).
 */
public interface UserService {
    /**
     * Создает нового пользователя на основе переданных данных.
     *
     * @param createUserDTO DTO с данными нового пользователя
     * @return DTO созданного пользователя
     */
    UserDTO createUser(CreateUserDTO createUserDTO);
    /**
     * Получает пользователя по его id.
     *
     * @param id идентификатор пользователя
     * @return Optional с UserDTO, если пользователь найден
     */
    Optional<UserDTO> getUserById(Long id);
    /**
     * Получает пользователя по логину.
     *
     * @param login логин пользователя
     * @return Optional с UserDTO, если пользователь найден
     */
    Optional<UserDTO> getUserByLogin(String login);
    /**
     * Возвращает список всех пользователей.
     *
     * @return список UserDTO всех пользователей
     */
    List<UserDTO> getAllUsers();
    /**
     * Обновляет данные существующего пользователя.
     *
     * @param id идентификатор пользователя для обновления
     * @param updateUserDTO DTO с обновленными данными
     * @return Optional с обновленным UserDTO, если пользователь найден
     */
    Optional<UserDTO> updateUser(Long id, CreateUserDTO updateUserDTO);
    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return true, если пользователь был удален; false, если не найден
     */
    boolean deleteUser(Long id);
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

