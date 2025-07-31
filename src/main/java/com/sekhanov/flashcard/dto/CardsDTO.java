package com.sekhanov.flashcard.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * DTO для передачи данных слова.
 * <p>
 * Содержит уникальный идентификатор слова, термин и его определение.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardsDTO {
    private Long id;
    private String term;
    private String definition;
}
