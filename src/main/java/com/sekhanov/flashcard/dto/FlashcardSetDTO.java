package com.sekhanov.flashcard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 * DTO для передачи данных списка слов.
 * <p>
 * Содержит идентификатор списка, его имя и список слов, входящих в этот список.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardSetDTO {
    private Long id;
    private String name;
    private String description;
    private List<CardsDTO> cards;
}
