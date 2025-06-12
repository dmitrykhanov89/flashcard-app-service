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
public class CreateWordListDTO {
    private String name;
    private List<CreateWordsDTO> words;
    private Long userId;
}
