package com.sekhanov.flashcard.controller;

import com.sekhanov.flashcard.dto.CreateWordListDTO;
import com.sekhanov.flashcard.dto.CreateWordsDTO;
import com.sekhanov.flashcard.dto.WordListDTO;
import com.sekhanov.flashcard.dto.WordsDTO;
import com.sekhanov.flashcard.service.WordListService;
import com.sekhanov.flashcard.service.WordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/word-lists")
public class WordListController {

    private final WordListService wordListService;
    private final WordsService wordsService;

    @Autowired
    public WordListController(WordListService wordListService, WordsService wordsService) {
        this.wordListService = wordListService;
        this.wordsService = wordsService;
    }

    @PostMapping
    public ResponseEntity<WordListDTO> createWordList(@RequestBody CreateWordListDTO createWordListDTO) {
        WordListDTO wordListDTO = wordListService.createWordList(createWordListDTO);
        return new ResponseEntity<>(wordListDTO, HttpStatus.CREATED);
    }

    @PostMapping("/{wordListId}/words")
    public ResponseEntity<WordsDTO> createWords(@PathVariable Long wordListId,
                                                @RequestBody CreateWordsDTO entryDTO) {
        WordsDTO wordsDTO = wordsService.createWords(wordListId, entryDTO);
        return new ResponseEntity<>(wordsDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WordListDTO> getWordListById(@PathVariable Long id) {
        Optional<WordListDTO> wordListDTO = wordListService.getWordListById(id);
        return wordListDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{wordListId}/words/{id}")
    public ResponseEntity<WordsDTO> getWordsById(@PathVariable Long id) {
        Optional<WordsDTO> wordsDTO = wordsService.getWordsById(id);
        return wordsDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<WordListDTO> getWordListByName(@PathVariable String name) {
        Optional<WordListDTO> wordListDTO = wordListService.getWordListByName(name);
        return wordListDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping
    public ResponseEntity<List<WordListDTO>> getAllWordLists() {
        List<WordListDTO> wordLists = wordListService.getAllWordLists();
        return ResponseEntity.ok(wordLists);
    }

    @GetMapping("/{wordListId}/words")
    public List<WordsDTO> getAllWordsForWordList(@PathVariable Long wordListId) {
        return wordsService.getAllWordsForWordList(wordListId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WordListDTO> updateWordList(@PathVariable Long id,
                                                      @RequestBody CreateWordListDTO updateDTO) {
        Optional<WordListDTO> updated = wordListService.updateWordList(id, updateDTO);
        return updated.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{wordListId}/words/{id}")
    public ResponseEntity<WordsDTO> updateWords(@PathVariable Long id,
                                                @RequestBody CreateWordsDTO entryDTO) {
        Optional<WordsDTO> updatedWords = wordsService.updateWords(id, entryDTO);
        return updatedWords.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWordList(@PathVariable Long id) {
        boolean deleted = wordListService.deleteWordList(id);
        return deleted ? ResponseEntity.noContent().build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{wordListId}/words/{id}")
    public ResponseEntity<Void> deleteWords(@PathVariable Long id) {
        if (wordsService.deleteWords(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
