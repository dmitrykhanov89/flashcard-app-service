package com.sekhanov.flashcard.repository;

import com.sekhanov.flashcard.entity.FlashcardSet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlashcardSetRepository extends JpaRepository<FlashcardSet, Long> {
    FlashcardSet findByName(String name);
}

