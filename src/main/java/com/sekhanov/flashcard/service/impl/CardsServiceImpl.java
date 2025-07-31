package com.sekhanov.flashcard.service.impl;

import com.sekhanov.flashcard.dto.CreateCardsDTO;
import com.sekhanov.flashcard.dto.CardsDTO;
import com.sekhanov.flashcard.entity.Cards;
import com.sekhanov.flashcard.entity.FlashcardSet;
import com.sekhanov.flashcard.repository.CardsRepository;
import com.sekhanov.flashcard.repository.FlashcardSetRepository;
import com.sekhanov.flashcard.service.CardsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * Предоставляет CRUD-функциональность для сущностей {@link Cards}, связанных со списками слов.
 * Используется для создания, получения, обновления и удаления отдельных слов, а также получения всех слов для заданного списка.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Создание нового слова и его привязка к списку слов</li>
 *     <li>Получение слова по ID</li>
 *     <li>Получение всех слов, принадлежащих конкретному списку</li>
 *     <li>Обновление содержимого слова</li>
 *     <li>Удаление слова</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class CardsServiceImpl implements CardsService {

    private final CardsRepository cardsRepository;
    private final FlashcardSetRepository flashcardSetRepository;

    @Override
    @Transactional
    public CardsDTO createCards(Long wordListId, CreateCardsDTO entryDTO) {
        FlashcardSet flashcardSet = flashcardSetRepository.findById(wordListId)
                .orElseThrow(() -> new RuntimeException("FlashcardSet not found"));

        Cards entry = new Cards();
        entry.setTerm(entryDTO.getTerm());
        entry.setDefinition(entryDTO.getDefinition());
        entry.setFlashcardSet(flashcardSet);

        entry = cardsRepository.save(entry);

        return toDTO(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CardsDTO> getCardsById(Long id) {
        return cardsRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardsDTO> getAllCardsForFlashcardSet(Long wordListId) {
        return cardsRepository.findByFlashcardSetId(wordListId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<CardsDTO> updateCards(Long id, CreateCardsDTO entryDTO) {
        Optional<Cards> optionalEntry = cardsRepository.findById(id);
        if (optionalEntry.isPresent()) {
            Cards entry = optionalEntry.get();
            entry.setTerm(entryDTO.getTerm());
            entry.setDefinition(entryDTO.getDefinition());

            entry = cardsRepository.save(entry);
            return Optional.of(toDTO(entry));
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public boolean deleteCards(Long id) {
        if (cardsRepository.existsById(id)) {
            cardsRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Преобразование WordListEntry в WordListEntryDTO
    private CardsDTO toDTO(Cards entry) {
        return new CardsDTO(entry.getId(), entry.getTerm(), entry.getDefinition());
    }
}
