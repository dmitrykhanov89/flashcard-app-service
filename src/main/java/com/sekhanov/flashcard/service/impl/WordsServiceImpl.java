package com.sekhanov.flashcard.service.impl;

import com.sekhanov.flashcard.dto.CreateWordsDTO;
import com.sekhanov.flashcard.dto.WordsDTO;
import com.sekhanov.flashcard.entity.WordList;
import com.sekhanov.flashcard.entity.Words;
import com.sekhanov.flashcard.repository.WordsRepository;
import com.sekhanov.flashcard.repository.WordListRepository;
import com.sekhanov.flashcard.service.WordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WordsServiceImpl implements WordsService {

    private final WordsRepository wordsRepository;
    private final WordListRepository wordListRepository;

    @Autowired
    public WordsServiceImpl(WordsRepository wordsRepository,
                            WordListRepository wordListRepository) {
        this.wordsRepository = wordsRepository;
        this.wordListRepository = wordListRepository;
    }

    @Override
    @Transactional
    public WordsDTO createWords(Long wordListId, CreateWordsDTO entryDTO) {
        WordList wordList = wordListRepository.findById(wordListId)
                .orElseThrow(() -> new RuntimeException("WordList not found"));

        Words entry = new Words();
        entry.setTerm(entryDTO.getTerm());
        entry.setDefinition(entryDTO.getDefinition());
        entry.setWordList(wordList);

        entry = wordsRepository.save(entry);

        return toDTO(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WordsDTO> getWordsById(Long id) {
        return wordsRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WordsDTO> getAllWordsForWordList(Long wordListId) {
        return wordsRepository.findByWordListId(wordListId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<WordsDTO> updateWords(Long id, CreateWordsDTO entryDTO) {
        Optional<Words> optionalEntry = wordsRepository.findById(id);
        if (optionalEntry.isPresent()) {
            Words entry = optionalEntry.get();
            entry.setTerm(entryDTO.getTerm());
            entry.setDefinition(entryDTO.getDefinition());

            entry = wordsRepository.save(entry);
            return Optional.of(toDTO(entry));
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public boolean deleteWords(Long id) {
        if (wordsRepository.existsById(id)) {
            wordsRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Преобразование WordListEntry в WordListEntryDTO
    private WordsDTO toDTO(Words entry) {
        return new WordsDTO(entry.getId(), entry.getTerm(), entry.getDefinition());
    }
}
