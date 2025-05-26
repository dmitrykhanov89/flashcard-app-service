package com.sekhanov.flashcard.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordsDTO {
    private Long id;
    private String term;
    private String definition;
}
