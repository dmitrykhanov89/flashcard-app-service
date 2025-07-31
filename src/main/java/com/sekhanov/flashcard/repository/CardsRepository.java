package com.sekhanov.flashcard.repository;

import com.sekhanov.flashcard.entity.Cards;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardsRepository extends JpaRepository<Cards, Long> {
    List<Cards> findByFlashcardSetId(Long flashcardSetId);
}