package com.sekhanov.flashcard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO для передачи данных о недавно открытом наборе карточек.
 * <p>
 * Содержит идентификатор и название набора карточек,
 * а также дату и время последнего открытия.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LastSeenFlashcardSetDto {
    private Long flashcardSetId;
    private String flashcardSetName;
    private LocalDateTime openedAt;
    private String ownerName;
    private int cardsCount;
}
