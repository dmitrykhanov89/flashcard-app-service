package com.sekhanov.flashcard.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

@Entity
@Table(name = "words")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "wordList")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Words {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String term;

    @Column(nullable = false)
    private String definition;

    @ManyToOne
    @JoinColumn(name = "word_list_id", nullable = false)
    @JsonBackReference
    private WordList wordList;
}
