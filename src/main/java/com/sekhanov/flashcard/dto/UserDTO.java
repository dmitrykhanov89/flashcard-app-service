package com.sekhanov.flashcard.dto;

import com.sekhanov.flashcard.entity.WordList;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String surname;
    private String login;

    private Set<WordList> wordLists;
}
