package com.sekhanov.flashcard.repository;

import com.sekhanov.flashcard.entity.Words;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordsRepository extends JpaRepository<Words, Long> {
    List<Words> findByWordListId(Long wordListId);
}