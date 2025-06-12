package com.sekhanov.flashcard.controller;

import com.sekhanov.flashcard.dto.CreateWordListDTO;
import com.sekhanov.flashcard.dto.CreateWordsDTO;
import com.sekhanov.flashcard.dto.WordListDTO;
import com.sekhanov.flashcard.dto.WordsDTO;
import com.sekhanov.flashcard.service.UserService;
import com.sekhanov.flashcard.service.WordListService;
import com.sekhanov.flashcard.service.WordsService;
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
@RequestMapping("/api/word-lists")
@RequiredArgsConstructor
public class WordListController {

    private final WordListService wordListService;
    private final WordsService wordsService;
    private final UserService userService;

    /**
     * Обрабатывает POST-запрос на создание нового списка слов.
     * <p>
     * Принимает данные для создания списка слов в виде {@link CreateWordListDTO},
     * создает новый список через сервис {@link WordListService} и возвращает
     * DTO созданного списка с HTTP статусом 201 Created.
     * </p>
     *
     * @param dto объект {@link CreateWordListDTO}, содержащий данные для создания списка слов.
     * @return {@link ResponseEntity} с объектом {@link WordListDTO} и статусом 201 Created.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WordListDTO createWordList(@RequestBody CreateWordListDTO dto) {
        return wordListService.createWordList(dto);
    }

    /**
     * Создает новое слово в списке слов с указанным идентификатором.
     *
     * @param wordListId ID списка слов, к которому добавляется слово.
     * @param dto   DTO с данными нового слова.
     * @return ResponseEntity с созданным словом и статусом 201 Created.
     */
    @PostMapping("/{wordListId}/words")
    @ResponseStatus(HttpStatus.CREATED)
    public WordsDTO createWords(@PathVariable Long wordListId, @RequestBody CreateWordsDTO dto) {
        return wordsService.createWords(wordListId, dto);
    }

    /**
     * Получает список слов по его ID.
     *
     * @param id ID списка слов.
     * @return ResponseEntity с DTO списка слов или статусом 404, если список не найден.
     */
    @GetMapping("/{id}")
    public WordListDTO getWordListById(@PathVariable Long id) {
        return wordListService.getWordListById(id)
                .orElseThrow(() -> new EntityNotFoundException("Список слов с id " + id + " не найден"));
    }

    /**
     * Получает слово по его ID.
     *
     * @param id ID слова.
     * @return ResponseEntity с DTO слова или статусом 404, если слово не найдено.
     */
    @GetMapping("/{wordListId}/words/{id}")
    public WordsDTO getWordsById(@PathVariable Long id) {
        return wordsService.getWordsById(id)
                .orElseThrow(() -> new EntityNotFoundException("Слово с id " + id + " не найдено"));
    }

    /**
     * Получает список слов по имени.
     *
     * @param name Имя списка слов.
     * @return ResponseEntity с DTO списка слов или статусом 404, если список не найден.
     */
    @GetMapping("/name/{name}")
    public WordListDTO getWordListByName(@PathVariable String name) {
        return wordListService.getWordListByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Список слов с именем '" + name + "' не найден"));
    }

    /**
     * Получает все списки слов.
     *
     * @return ResponseEntity со списком DTO всех списков слов.
     */
    @GetMapping
    public List<WordListDTO> getAllWordLists() {
        return wordListService.getAllWordLists();
    }

    /**
     * Получает все слова для указанного списка слов.
     *
     * @param wordListId ID списка слов.
     * @return Список DTO всех слов в списке.
     */
    @GetMapping("/{wordListId}/words")
    public List<WordsDTO> getAllWordsForWordList(@PathVariable Long wordListId) {
        return wordsService.getAllWordsForWordList(wordListId);
    }

    /**
     * Обновляет список слов с указанным ID.
     *
     * @param id        ID списка слов для обновления.
     * @param dto DTO с новыми данными списка слов.
     * @return ResponseEntity с обновленным DTO списка слов или статусом 404, если список не найден.
     */
    @PutMapping("/{id}")
    public WordListDTO updateWordList(@PathVariable Long id, @RequestBody CreateWordListDTO dto) {
        return wordListService.updateWordList(id, dto)
                .orElseThrow(() -> new EntityNotFoundException("Список слов с id " + id + " не найден"));
    }

    /**
     * Обновляет слово с указанным ID.
     *
     * @param id       ID слова для обновления.
     * @param dto DTO с новыми данными слова.
     * @return ResponseEntity с обновленным DTO слова или статусом 404, если слово не найдено.
     */
    @PutMapping("/{wordListId}/words/{id}")
    public WordsDTO updateWords(@PathVariable Long id, @RequestBody CreateWordsDTO dto) {
        return wordsService.updateWords(id, dto)
                .orElseThrow(() -> new EntityNotFoundException("Слово с id " + id + " не найдено"));
    }

    /**
     * Удаляет список слов по ID.
     *
     * @param id ID списка слов для удаления.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWordList(@PathVariable Long id) {
        if (!wordListService.deleteWordList(id)) {
            throw new EntityNotFoundException("Список слов с id " + id + " не найден");
        }
    }

    /**
     * Удаляет слово по ID.
     *
     * @param id ID слова для удаления.
     */
    @DeleteMapping("/{wordListId}/words/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWords(@PathVariable Long id) {
        if (!wordsService.deleteWords(id)) {
            throw new EntityNotFoundException("Слово с id " + id + " не найдено");
        }
    }

    /**
     * Добавляет список слов к пользователю.
     *
     * @param userId     ID пользователя.
     * @param wordListId ID списка слов.
     */
    @PostMapping("/{userId}/add/{wordListId}")
    public void addWordListToUser(@PathVariable Long userId, @PathVariable Long wordListId) {
        if (!wordListService.addWordListToUser(userId, wordListId)) {
            throw new EntityNotFoundException("Пользователь или список слов не найден");
        }
    }

    /**
     * Удаляет список слов у пользователя.
     *
     * @param userId     ID пользователя.
     * @param wordListId ID списка слов.
     */
    @DeleteMapping("/{userId}/remove/{wordListId}")
    public void removeWordListFromUser(@PathVariable Long userId, @PathVariable Long wordListId) {
        if (!wordListService.removeWordListFromUser(userId, wordListId)) {
            throw new EntityNotFoundException("Пользователь или список слов не найден");
        }
    }
}
