package com.sekhanov.flashcard.service.impl;

import com.sekhanov.flashcard.dto.CreateCardsDTO;
import com.sekhanov.flashcard.dto.CardsDTO;
import com.sekhanov.flashcard.entity.Cards;
import com.sekhanov.flashcard.entity.FlashcardSet;
import com.sekhanov.flashcard.repository.CardsRepository;
import com.sekhanov.flashcard.repository.FlashcardSetRepository;
import com.sekhanov.flashcard.service.CardsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
@RequiredArgsConstructor
public class CardsServiceImpl implements CardsService {

    private final CardsRepository cardsRepository;
    private final FlashcardSetRepository flashcardSetRepository;

    @Override
    @Transactional
    public CardsDTO createCards(Long wordListId, CreateCardsDTO entryDTO) {
        log.debug("Создание карточки для набора id={}", wordListId);
        FlashcardSet flashcardSet = flashcardSetRepository.findById(wordListId)
                .orElseThrow(() -> {
                    log.warn("Набор карточек id={} не найден", wordListId);
                    return new RuntimeException("FlashcardSet not found");
                });

        Cards entry = new Cards();
        entry.setTerm(entryDTO.getTerm());
        entry.setDefinition(entryDTO.getDefinition());
        entry.setFlashcardSet(flashcardSet);
        entry = cardsRepository.save(entry);
        log.info("Создана карточка id={} для набора id={}", entry.getId(), wordListId);

        return toDTO(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CardsDTO> getCardsById(Long id) {
        log.debug("Получение карточки по id={}", id);
        Optional<CardsDTO> result = cardsRepository.findById(id).map(this::toDTO);
        if (result.isEmpty()) {
            log.warn("Карточка id={} не найдена", id);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardsDTO> getAllCardsForFlashcardSet(Long wordListId) {
        log.debug("Получение всех карточек для набора id={}", wordListId);
        List<CardsDTO> cards = cardsRepository.findByFlashcardSetId(wordListId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        log.info("Найдено {} карточек для набора id={}", cards.size(), wordListId);
        return cards;
    }

    @Override
    @Transactional
    public Optional<CardsDTO> updateCards(Long id, CreateCardsDTO entryDTO) {
        log.debug("Обновление карточки id={}", id);
        Optional<Cards> optionalEntry = cardsRepository.findById(id);
        if (optionalEntry.isPresent()) {
            Cards entry = optionalEntry.get();
            entry.setTerm(entryDTO.getTerm());
            entry.setDefinition(entryDTO.getDefinition());
            entry = cardsRepository.save(entry);
            log.info("Карточка id={} успешно обновлена", id);
            return Optional.of(toDTO(entry));
        }
        log.warn("Не удалось обновить карточку: id={} не найден", id);
        return Optional.empty();
    }

    @Override
    @Transactional
    public boolean deleteCards(Long id) {
        log.debug("Удаление карточки id={}", id);
        if (cardsRepository.existsById(id)) {
            cardsRepository.deleteById(id);
            log.info("Карточка id={} удалена", id);
            return true;
        }
        log.warn("Карточка id={} не найдена для удаления", id);
        return false;
    }

    private CardsDTO toDTO(Cards entry) {
        return new CardsDTO(entry.getId(), entry.getTerm(), entry.getDefinition());
    }
}