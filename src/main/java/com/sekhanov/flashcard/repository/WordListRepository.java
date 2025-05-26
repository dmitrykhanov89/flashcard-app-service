package com.sekhanov.flashcard.repository;

import com.sekhanov.flashcard.entity.WordList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordListRepository extends JpaRepository<WordList, Long> {
    WordList findByName(String name);
}

