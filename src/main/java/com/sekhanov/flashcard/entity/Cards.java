package com.sekhanov.flashcard.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

@Entity
@Table(name = "cards") // ✅ новое имя таблицы
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "flashcardSet")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cards {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String term;

    @Column(nullable = false)
    private String definition;

    @ManyToOne
    @JoinColumn(name = "flashcard_set_id", nullable = false) // ✅ новое имя колонки
    @JsonBackReference
    private FlashcardSet flashcardSet;
}
