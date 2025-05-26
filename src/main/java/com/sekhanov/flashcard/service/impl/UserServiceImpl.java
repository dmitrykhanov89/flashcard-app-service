package com.sekhanov.flashcard.service.impl;

import com.sekhanov.flashcard.dto.CreateUserDTO;
import com.sekhanov.flashcard.dto.UserDTO;
import com.sekhanov.flashcard.entity.User;
import com.sekhanov.flashcard.entity.WordList;
import com.sekhanov.flashcard.repository.UserRepository;
import com.sekhanov.flashcard.repository.WordListRepository;
import com.sekhanov.flashcard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WordListRepository wordListRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, WordListRepository wordListRepository) {
        this.userRepository = userRepository;
        this.wordListRepository = wordListRepository;
    }

    @Override
    @Transactional
    public UserDTO createUser(CreateUserDTO createUserDTO) {
        User user = new User();
        user.setName(createUserDTO.getName());
        user.setSurname(createUserDTO.getSurname());
        user.setLogin(createUserDTO.getLogin());
        user.setPassword(createUserDTO.getPassword());

        user = userRepository.save(user);

        return toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserByLogin(String login) {
        User user = userRepository.findByLogin(login);
        return Optional.ofNullable(user).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<UserDTO> updateUser(Long id, CreateUserDTO updateUserDTO) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setName(updateUserDTO.getName());
            user.setSurname(updateUserDTO.getSurname());
            user.setLogin(updateUserDTO.getLogin());
            user.setPassword(updateUserDTO.getPassword());

            user = userRepository.save(user);
            return Optional.of(toDTO(user));
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean addWordListToUser(Long userId, Long wordListId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<WordList> optionalWordList = wordListRepository.findById(wordListId);

        if (optionalUser.isPresent() && optionalWordList.isPresent()) {
            User user = optionalUser.get();
            WordList wordList = optionalWordList.get();

            // Избегаем повторного добавления
            if (!user.getWordLists().contains(wordList)) {
                user.getWordLists().add(wordList);
                userRepository.save(user);
            }

            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public boolean removeWordListFromUser(Long userId, Long wordListId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<WordList> optionalWordList = wordListRepository.findById(wordListId);

        if (optionalUser.isPresent() && optionalWordList.isPresent()) {
            User user = optionalUser.get();
            WordList wordList = optionalWordList.get();

            if (user.getWordLists().contains(wordList)) {
                user.getWordLists().remove(wordList);
                userRepository.save(user);
            }

            return true;
        }

        return false;
    }

    // Преобразование User в UserDTO
    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setLogin(user.getLogin());
        dto.setWordLists(user.getWordLists());
        return dto;
    }
}
