package com.sekhanov.flashcard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordListDTO {
    private Long id;
    private String name;
    private List<WordsDTO> words;
}
