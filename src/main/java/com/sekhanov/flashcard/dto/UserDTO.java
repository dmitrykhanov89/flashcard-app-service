package com.sekhanov.flashcard.dto;

import com.sekhanov.flashcard.entity.FlashcardSet;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;

/**
 * DTO для передачи данных пользователя.
 * <p>
 * Содержит основную информацию о пользователе, включая идентификатор,
 * имя, фамилию, логин, а также набор списков слов, связанных с пользователем.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String surname;
    private String login;
}
