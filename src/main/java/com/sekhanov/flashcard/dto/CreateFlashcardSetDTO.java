package com.sekhanov.flashcard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * DTO для создания нового списка слов.
 * <p>
 * Содержит название списка, список слов и ID пользователя, которому принадлежит список.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFlashcardSetDTO {
    private String name;
    private List<CreateCardsDTO> cards;
    private Long userId;
}
