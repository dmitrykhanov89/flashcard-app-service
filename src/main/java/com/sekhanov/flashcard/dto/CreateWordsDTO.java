package com.sekhanov.flashcard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateWordsDTO {
    private String term;
    private String definition;
}
