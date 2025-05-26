package com.sekhanov.flashcard.service.impl;

import com.sekhanov.flashcard.dto.*;
import com.sekhanov.flashcard.entity.WordList;
import com.sekhanov.flashcard.entity.Words;
import com.sekhanov.flashcard.repository.UserRepository;
import com.sekhanov.flashcard.repository.WordListRepository;
import com.sekhanov.flashcard.service.WordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WordListServiceImpl implements WordListService {

    private final WordListRepository wordListRepository;
    private final UserRepository userRepository;

    @Autowired
    public WordListServiceImpl(WordListRepository wordListRepository, UserRepository userRepository) {
        this.wordListRepository = wordListRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public WordListDTO createWordList(CreateWordListDTO dto) {
        WordList wordList = new WordList();
        wordList.setName(dto.getName());

        // 1) Привязываем пользователя
        userRepository.findById(dto.getUserId()).ifPresent(user -> {
            wordList.getUsers().add(user);
            user.getWordLists().add(wordList);
        });

        // 2) Добавляем слова (как было)
        if (dto.getWords() != null) {
            for (CreateWordsDTO e : dto.getWords()) {
                Words w = new Words();
                w.setTerm(e.getTerm());
                w.setDefinition(e.getDefinition());
                w.setWordList(wordList);
                wordList.getEntries().add(w);
            }
        }

        WordList saved = wordListRepository.save(wordList);
        return toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WordListDTO> getWordListById(Long id) {
        return wordListRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WordListDTO> getWordListByName(String name) {
        WordList wordList = wordListRepository.findByName(name);
        return Optional.ofNullable(wordList).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WordListDTO> getAllWordLists() {
        List<WordList> wordLists = wordListRepository.findAll();
        return wordLists.stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public Optional<WordListDTO> updateWordList(Long id, CreateWordListDTO updateDTO) {
        Optional<WordList> optionalWordList = wordListRepository.findById(id);
        if (optionalWordList.isEmpty()) {
            return Optional.empty();
        }

        WordList wordList = optionalWordList.get();
        wordList.setName(updateDTO.getName());

        // обновляем СЛОВА ТОЛЬКО если пришёл непустой список
        if (updateDTO.getWords() != null && !updateDTO.getWords().isEmpty()) {
            wordList.getEntries().clear();
            for (CreateWordsDTO entryDTO : updateDTO.getWords()) {
                Words entry = new Words();
                entry.setTerm(entryDTO.getTerm());
                entry.setDefinition(entryDTO.getDefinition());
                entry.setWordList(wordList);
                wordList.getEntries().add(entry);
            }
        }

        WordList updated = wordListRepository.save(wordList);
        return Optional.of(toDTO(updated));
    }

    @Override
    @Transactional
    public boolean deleteWordList(Long id) {
        if (wordListRepository.existsById(id)) {
            wordListRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private WordListDTO toDTO(WordList wordList) {
        WordListDTO dto = new WordListDTO();
        dto.setId(wordList.getId());
        dto.setName(wordList.getName());
        dto.setWords(wordList.getEntries().stream()
                .map(entry -> new WordsDTO(entry.getId(), entry.getTerm(), entry.getDefinition()))
                .toList());
        return dto;
    }
}
