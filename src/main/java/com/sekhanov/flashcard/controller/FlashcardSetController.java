package com.sekhanov.flashcard.controller;

import com.sekhanov.flashcard.dto.*;
import com.sekhanov.flashcard.service.LastSeenFlashcardSetService;
import com.sekhanov.flashcard.service.UserService;
import com.sekhanov.flashcard.service.FlashcardSetService;
import com.sekhanov.flashcard.service.CardsService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для управления списками слов и словами внутри этих списков.
 * <p>
 * Предоставляет эндпоинты для создания, чтения, обновления и удаления списков слов и слов,
 * а также для связывания списков слов с пользователями.
 * </p>
 * <p>
 * Все запросы к этому контроллеру начинаются с пути {@code /api/word-lists}.
 * </p>
 */
@RestController
@RequestMapping("/api/flashcardSet")
@RequiredArgsConstructor
public class FlashcardSetController {

    private final FlashcardSetService flashcardSetService;
    private final CardsService cardsService;
    private final UserService userService;
    private final LastSeenFlashcardSetService lastSeenFlashcardSetService;

    /**
     * Обрабатывает POST-запрос на создание нового списка слов.
     * <p>
     * Принимает данные для создания списка слов в виде {@link CreateFlashcardSetDTO},
     * создает новый список через сервис {@link FlashcardSetService} и возвращает
     * DTO созданного списка с HTTP статусом 201 Created.
     * </p>
     *
     * @param dto объект {@link CreateFlashcardSetDTO}, содержащий данные для создания списка слов.
     * @return {@link ResponseEntity} с объектом {@link FlashcardSetDTO} и статусом 201 Created.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FlashcardSetDTO createFlashcardSet(@RequestBody CreateFlashcardSetDTO dto) {
        return flashcardSetService.createFlashcardSet(dto);
    }

    /**
     * Создает новое слово в списке слов с указанным идентификатором.
     *
     * @param flashcardSetId ID списка слов, к которому добавляется слово.
     * @param dto   DTO с данными нового слова.
     * @return ResponseEntity с созданным словом и статусом 201 Created.
     */
    @PostMapping("/{flashcardSetId}/cards")
    @ResponseStatus(HttpStatus.CREATED)
    public CardsDTO createCards(@PathVariable Long flashcardSetId, @RequestBody CreateCardsDTO dto) {
        return cardsService.createCards(flashcardSetId, dto);
    }

    /**
     * Получает список слов по его ID.
     *
     * @param id ID списка слов.
     * @return ResponseEntity с DTO списка слов или статусом 404, если список не найден.
     */
    @GetMapping("/{id}")
    public FlashcardSetDTO getFlashcardSetById(@PathVariable Long id) {
        return flashcardSetService.getFlashcardSetById(id)
                .orElseThrow(() -> new EntityNotFoundException("Список слов с id " + id + " не найден"));
    }

    /**
     * Получает слово по его ID.
     *
     * @param id ID слова.
     * @return ResponseEntity с DTO слова или статусом 404, если слово не найдено.
     */
    @GetMapping("/{flashcardSetId}/cards/{id}")
    public CardsDTO getCardsById(@PathVariable Long id) {
        return cardsService.getCardsById(id)
                .orElseThrow(() -> new EntityNotFoundException("Слово с id " + id + " не найдено"));
    }

    /**
     * Получает список слов по имени.
     *
     * @param name Имя списка слов.
     * @return ResponseEntity с DTO списка слов или статусом 404, если список не найден.
     */
    @GetMapping("/name/{name}")
    public FlashcardSetDTO getFlashcardSetByName(@PathVariable String name) {
        return flashcardSetService.getFlashcardSetByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Список слов с именем '" + name + "' не найден"));
    }

    /**
     * Получает все списки слов.
     *
     * @return ResponseEntity со списком DTO всех списков слов.
     */
    @GetMapping
    public List<FlashcardSetDTO> getAllFlashcardSet() {
        return flashcardSetService.getAllFlashcardSet();
    }

    /**
     * Получает все слова для указанного списка слов.
     *
     * @param flashcardSetId ID списка слов.
     * @return Список DTO всех слов в списке.
     */
    @GetMapping("/{flashcardSetId}/cards")
    public List<CardsDTO> getAllCardsForFlashcardSet(@PathVariable Long flashcardSetId) {
        return cardsService.getAllCardsForFlashcardSet(flashcardSetId);
    }

    /**
     * Обновляет список слов с указанным ID.
     *
     * @param id        ID списка слов для обновления.
     * @param dto DTO с новыми данными списка слов.
     * @return ResponseEntity с обновленным DTO списка слов или статусом 404, если список не найден.
     */
    @PutMapping("/{id}")
    public FlashcardSetDTO updateFlashcardSet(@PathVariable Long id, @RequestBody CreateFlashcardSetDTO dto) {
        return flashcardSetService.updateFlashcardSet(id, dto)
                .orElseThrow(() -> new EntityNotFoundException("Список слов с id " + id + " не найден"));
    }

    /**
     * Обновляет слово с указанным ID.
     *
     * @param id       ID слова для обновления.
     * @param dto DTO с новыми данными слова.
     * @return ResponseEntity с обновленным DTO слова или статусом 404, если слово не найдено.
     */
    @PutMapping("/{flashcardSetId}/cards/{id}")
    public CardsDTO updateCards(@PathVariable Long id, @RequestBody CreateCardsDTO dto) {
        return cardsService.updateCards(id, dto)
                .orElseThrow(() -> new EntityNotFoundException("Слово с id " + id + " не найдено"));
    }

    /**
     * Удаляет список слов по ID.
     *
     * @param id ID списка слов для удаления.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFlashcardSet(@PathVariable Long id) {
        if (!flashcardSetService.deleteFlashcardSet(id)) {
            throw new EntityNotFoundException("Список слов с id " + id + " не найден");
        }
    }

    /**
     * Удаляет слово по ID.
     *
     * @param id ID слова для удаления.
     */
    @DeleteMapping("/{flashcardSetId}/cards/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCards(@PathVariable Long id) {
        if (!cardsService.deleteCards(id)) {
            throw new EntityNotFoundException("Слово с id " + id + " не найдено");
        }
    }

    /**
     * Добавляет список слов к пользователю.
     *
     * @param userId     ID пользователя.
     * @param flashcardSetId ID списка слов.
     */
    @PostMapping("/{userId}/add/{flashcardSetId}")
    public void addFlashcardSetToUser(@PathVariable Long userId, @PathVariable Long flashcardSetId) {
        if (!flashcardSetService.addFlashcardSetToUser(userId, flashcardSetId)) {
            throw new EntityNotFoundException("Пользователь или список слов не найден");
        }
    }

    /**
     * Удаляет список слов у пользователя.
     *
     * @param userId     ID пользователя.
     * @param flashcardSetId ID списка слов.
     */
    @DeleteMapping("/{userId}/remove/{flashcardSetId}")
    public void removeFlashcardSetFromUser(@PathVariable Long userId, @PathVariable Long flashcardSetId) {
        if (!flashcardSetService.removeFlashcardSetFromUser(userId, flashcardSetId)) {
            throw new EntityNotFoundException("Пользователь или список слов не найден");
        }
    }

    /**
     * Получает список из 10 последних наборов карточек текущего пользователя.
     *
     * @return Список DTO последних наборов карточек.
     */
    @GetMapping("/LastSeenSets")
    public List<LastSeenFlashcardSetDto> getLastSeenSets() {
        return lastSeenFlashcardSetService.getLastSeenSetsForCurrentUser();
    }
}
