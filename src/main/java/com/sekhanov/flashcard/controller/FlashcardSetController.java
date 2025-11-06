package com.sekhanov.flashcard.controller;

import com.sekhanov.flashcard.dto.*;
import com.sekhanov.flashcard.service.LastSeenFlashcardSetService;
import com.sekhanov.flashcard.service.FlashcardSetService;
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
     * Получает список из 10 последних наборов карточек текущего пользователя.
     *
     * @return Список DTO последних наборов карточек.
     */
    @GetMapping("/LastSeenSets")
    public List<LastSeenFlashcardSetDto> getLastSeenSets() {
        return lastSeenFlashcardSetService.getLastSeenSetsForCurrentUser();
    }

    /**
     * Получить все наборы карточек, созданные конкретным пользователем (владельцем).
     *
     * @param ownerId ID пользователя-владельца.
     * @return Список DTO наборов карточек.
     */
    @GetMapping("/owner/{ownerId}")
    public List<FlashcardSetDTO> getFlashcardSetsByOwner(@PathVariable Long ownerId) {
        return flashcardSetService.getFlashcardSetsByOwnerId(ownerId);
    }
}
