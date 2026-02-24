package com.sekhanov.flashcard.service.impl;

import com.sekhanov.flashcard.dto.*;
import com.sekhanov.flashcard.entity.Cards;
import com.sekhanov.flashcard.entity.FlashcardSet;
import com.sekhanov.flashcard.entity.User;
import com.sekhanov.flashcard.repository.UserRepository;
import com.sekhanov.flashcard.repository.FlashcardSetRepository;
import com.sekhanov.flashcard.service.FlashcardSetService;
import com.sekhanov.flashcard.service.LastSeenFlashcardSetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
@RequiredArgsConstructor
public class FlashcardSetServiceImpl implements FlashcardSetService {

    private final FlashcardSetRepository flashcardSetRepository;
    private final UserRepository userRepository;
    private final LastSeenFlashcardSetService lastSeenFlashcardSetService;

    private List<Cards> mapCards(List<CreateCardsDTO> cardDTOs, FlashcardSet flashcardSet) {
        return cardDTOs.stream()
                .map(dto -> {
                    Cards card = new Cards();
                    card.setTerm(dto.getTerm());
                    card.setDefinition(dto.getDefinition());
                    card.setFlashcardSet(flashcardSet);
                    return card;
                })
                .toList();
    }

    @Override
    @Transactional
    public FlashcardSetDTO createFlashcardSet(CreateFlashcardSetDTO dto) {
        log.debug("Попытка создать набор карточек для пользователя id={}", dto.getUserId());

        User owner = userRepository.findById(dto.getUserId()).orElseThrow(() -> {
                    log.warn("Не найден пользователь с id={}", dto.getUserId());
                    return new IllegalArgumentException("User not found");
                });

        FlashcardSet flashcardSet = new FlashcardSet();
        flashcardSet.setName(dto.getName());
        flashcardSet.setDescription(dto.getDescription());
        flashcardSet.setOwner(owner);
        flashcardSet.getUsers().add(owner);
        owner.getFlashcardSets().add(flashcardSet);

        if (dto.getCards() != null && !dto.getCards().isEmpty()) {
            flashcardSet.getCards().addAll(mapCards(dto.getCards(), flashcardSet));
        }

        FlashcardSet saved = flashcardSetRepository.save(flashcardSet);
        lastSeenFlashcardSetService.saveLastSeenSet(saved.getId());
        log.info("Создан набор карточек id={} для пользователя id={}", saved.getId(), owner.getId());

        return toDTO(saved);
    }

    @Override
    @Transactional
    public Optional<FlashcardSetDTO> getFlashcardSetById(Long id) {
        log.debug("Получение набора карточек по id={}", id);
        lastSeenFlashcardSetService.saveLastSeenSet(id);
        return flashcardSetRepository.findById(id)
                .map(set -> {
                    log.debug("Найден набор карточек: {}", set.getName());
                    return toDTO(set);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FlashcardSetDTO> getFlashcardSetByName(String name) {
        log.debug("Поиск набора карточек по имени={}", name);
        FlashcardSet flashcardSet = flashcardSetRepository.findByName(name);
        if (flashcardSet == null) {
            log.warn("Набор карточек с именем '{}' не найден", name);
            return Optional.empty();
        }
        return Optional.of(toDTO(flashcardSet));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashcardSetDTO> getAllFlashcardSet() {
        List<FlashcardSet> flashcardSets = flashcardSetRepository.findAll();
        log.debug("Получено {} наборов карточек", flashcardSets.size());
        return flashcardSets.stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public Optional<FlashcardSetDTO> updateFlashcardSet(Long id, CreateFlashcardSetDTO updateDTO) {
        log.debug("Обновление набора карточек id={}", id);
        return flashcardSetRepository.findById(id)
                .map(flashcardSet -> {
                    flashcardSet.setName(updateDTO.getName());
                    flashcardSet.setDescription(updateDTO.getDescription());

                    if (updateDTO.getCards() != null && !updateDTO.getCards().isEmpty()) {
                        flashcardSet.getCards().clear();
                        flashcardSet.getCards().addAll(mapCards(updateDTO.getCards(), flashcardSet));
                        log.trace("Набор с id={} обновился со следующим набором карточек {}", id, updateDTO.getCards());
                    }

                    FlashcardSet updated = flashcardSetRepository.save(flashcardSet);
                    log.info("Набор карточек id={} успешно обновлён", updated.getId());
                    return toDTO(updated);
                });
    }

    @Override
    @Transactional
    public boolean deleteFlashcardSet(Long id) {
        if (!flashcardSetRepository.existsById(id)) {
            log.warn("Попытка удалить несуществующий набор карточек id={}", id);
            return false;
        }
        // Сначала убрать связи ManyToMany с пользователями
        flashcardSetRepository.findById(id).ifPresent(flashcardSet -> {
            flashcardSet.getUsers().forEach(user -> user.getFlashcardSets().remove(flashcardSet));
            flashcardSet.getUsers().clear();
        });
        flashcardSetRepository.deleteById(id);
        log.info("Удалён набор карточек id={}", id);
        return true;
    }

    @Override
    @Transactional
    public boolean addFlashcardSetToUser(Long userId, Long wordListId) {
        log.debug("Добавление набора карточек id={} пользователю id={}", wordListId, userId);

        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<FlashcardSet> optionalWordList = flashcardSetRepository.findById(wordListId);

        if (optionalUser.isEmpty() || optionalWordList.isEmpty()) {
            log.warn("Не удалось добавить набор: пользователь или набор не найден (userId={}, setId={})", userId, wordListId);
            return false;
        }

        User user = optionalUser.get();
        FlashcardSet flashcardSet = optionalWordList.get();

        if (!user.getFlashcardSets().contains(flashcardSet)) {
            user.getFlashcardSets().add(flashcardSet);
            userRepository.save(user);
            log.info("Набор карточек id={} добавлен пользователю id={}", wordListId, userId);
        } else {
            log.debug("Набор карточек id={} уже привязан к пользователю id={}", wordListId, userId);
        }

        return true;
    }

    @Override
    @Transactional
    public boolean removeFlashcardSetFromUser(Long userId, Long wordListId) {
        log.debug("Удаление набора карточек id={} у пользователя id={}", wordListId, userId);

        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<FlashcardSet> optionalWordList = flashcardSetRepository.findById(wordListId);

        if (optionalUser.isEmpty() || optionalWordList.isEmpty()) {
            log.warn("Не удалось удалить набор: пользователь или набор не найден (userId={}, setId={})", userId, wordListId);
            return false;
        }

        User user = optionalUser.get();
        FlashcardSet flashcardSet = optionalWordList.get();

        if (user.getFlashcardSets().contains(flashcardSet)) {
            user.getFlashcardSets().remove(flashcardSet);
            userRepository.save(user);
            log.info("Набор карточек id={} удалён у пользователя id={}", wordListId, userId);
        } else {
            log.debug("У пользователя id={} не найден набор карточек id={}", userId, wordListId);
        }

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashcardSetDTO> getFlashcardSetsByOwnerId(Long ownerId) {
        List<FlashcardSet> sets = flashcardSetRepository.findByOwnerId(ownerId);
        log.debug("Найдено {} наборов карточек для владельца id={}", sets.size(), ownerId);
        return sets.stream().map(this::toDTO).toList();
    }

    private FlashcardSetDTO toDTO(FlashcardSet flashcardSet) {
        FlashcardSetDTO dto = new FlashcardSetDTO();
        dto.setId(flashcardSet.getId());
        dto.setName(flashcardSet.getName());
        dto.setDescription(flashcardSet.getDescription());
        dto.setCards(flashcardSet.getCards().stream()
                .map(entry -> new CardsDTO(entry.getId(), entry.getTerm(), entry.getDefinition()))
                .toList());
        dto.setOwnerName(flashcardSet.getOwner().getName() + " " + flashcardSet.getOwner().getSurname());
        return dto;
    }
}
