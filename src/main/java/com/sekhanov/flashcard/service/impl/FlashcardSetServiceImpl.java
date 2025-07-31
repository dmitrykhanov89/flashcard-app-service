package com.sekhanov.flashcard.service.impl;

import com.sekhanov.flashcard.dto.*;
import com.sekhanov.flashcard.entity.Cards;
import com.sekhanov.flashcard.entity.FlashcardSet;
import com.sekhanov.flashcard.entity.User;
import com.sekhanov.flashcard.repository.UserRepository;
import com.sekhanov.flashcard.repository.FlashcardSetRepository;
import com.sekhanov.flashcard.service.FlashcardSetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * Обеспечивает CRUD-операции и дополнительные функции, связанные с управлением списками слов,
 * включая создание, обновление, удаление, получение и привязку списков к пользователям.
 * </p>
 *
 * <p>Ключевые функции:</p>
 * <ul>
 *     <li>Создание нового списка слов с привязкой к пользователю</li>
 *     <li>Поиск списка по ID или имени</li>
 *     <li>Получение всех списков слов</li>
 *     <li>Обновление списка слов (включая полную замену списка слов)</li>
 *     <li>Удаление списка слов</li>
 *     <li>Добавление списка слов к пользователю</li>
 *     <li>Удаление списка слов у пользователя</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class FlashcardSetServiceImpl implements FlashcardSetService {

    private final FlashcardSetRepository flashcardSetRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public FlashcardSetDTO createFlashcardSet(CreateFlashcardSetDTO dto) {
        FlashcardSet flashcardSet = new FlashcardSet();
        flashcardSet.setName(dto.getName());

        // 1) Привязываем пользователя
        userRepository.findById(dto.getUserId()).ifPresent(user -> {
            flashcardSet.setOwner(user);
            flashcardSet.getUsers().add(user);
            user.getFlashcardSets().add(flashcardSet);
        });

        // 2) Добавляем слова (как было)
        if (dto.getCards() != null) {
            for (CreateCardsDTO e : dto.getCards()) {
                Cards w = new Cards();
                w.setTerm(e.getTerm());
                w.setDefinition(e.getDefinition());
                w.setFlashcardSet(flashcardSet);
                flashcardSet.getCards().add(w);
            }
        }

        FlashcardSet saved = flashcardSetRepository.save(flashcardSet);
        return toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FlashcardSetDTO> getFlashcardSetById(Long id) {
        return flashcardSetRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FlashcardSetDTO> getFlashcardSetByName(String name) {
        FlashcardSet flashcardSet = flashcardSetRepository.findByName(name);
        return Optional.ofNullable(flashcardSet).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashcardSetDTO> getAllFlashcardSet() {
        List<FlashcardSet> flashcardSets = flashcardSetRepository.findAll();
        return flashcardSets.stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public Optional<FlashcardSetDTO> updateFlashcardSet(Long id, CreateFlashcardSetDTO updateDTO) {
        Optional<FlashcardSet> optionalWordList = flashcardSetRepository.findById(id);
        if (optionalWordList.isEmpty()) {
            return Optional.empty();
        }

        FlashcardSet flashcardSet = optionalWordList.get();
        flashcardSet.setName(updateDTO.getName());

        // обновляем СЛОВА ТОЛЬКО если пришёл непустой список
        if (updateDTO.getCards() != null && !updateDTO.getCards().isEmpty()) {
            flashcardSet.getCards().clear();
            for (CreateCardsDTO entryDTO : updateDTO.getCards()) {
                Cards entry = new Cards();
                entry.setTerm(entryDTO.getTerm());
                entry.setDefinition(entryDTO.getDefinition());
                entry.setFlashcardSet(flashcardSet);
                flashcardSet.getCards().add(entry);
            }
        }

        FlashcardSet updated = flashcardSetRepository.save(flashcardSet);
        return Optional.of(toDTO(updated));
    }

    @Override
    @Transactional
    public boolean deleteFlashcardSet(Long id) {
        if (flashcardSetRepository.existsById(id)) {
            flashcardSetRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean addFlashcardSetToUser(Long userId, Long wordListId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<FlashcardSet> optionalWordList = flashcardSetRepository.findById(wordListId);

        if (optionalUser.isPresent() && optionalWordList.isPresent()) {
            User user = optionalUser.get();
            FlashcardSet flashcardSet = optionalWordList.get();

            // Избегаем повторного добавления
            if (!user.getFlashcardSets().contains(flashcardSet)) {
                user.getFlashcardSets().add(flashcardSet);
                userRepository.save(user);
            }

            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public boolean removeFlashcardSetFromUser(Long userId, Long wordListId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<FlashcardSet> optionalWordList = flashcardSetRepository.findById(wordListId);

        if (optionalUser.isPresent() && optionalWordList.isPresent()) {
            User user = optionalUser.get();
            FlashcardSet flashcardSet = optionalWordList.get();

            if (user.getFlashcardSets().contains(flashcardSet)) {
                user.getFlashcardSets().remove(flashcardSet);
                userRepository.save(user);
            }

            return true;
        }

        return false;
    }

    private FlashcardSetDTO toDTO(FlashcardSet flashcardSet) {
        FlashcardSetDTO dto = new FlashcardSetDTO();
        dto.setId(flashcardSet.getId());
        dto.setName(flashcardSet.getName());
        dto.setCards(flashcardSet.getCards().stream()
                .map(entry -> new CardsDTO(entry.getId(), entry.getTerm(), entry.getDefinition()))
                .toList());
        return dto;
    }
}
