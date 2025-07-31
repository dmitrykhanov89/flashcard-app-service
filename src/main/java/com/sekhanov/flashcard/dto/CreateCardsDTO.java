package com.sekhanov.flashcard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO для создания нового слова.
 * <p>
 * Содержит термин и его определение.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCardsDTO {
    private String term;
    private String definition;
}
