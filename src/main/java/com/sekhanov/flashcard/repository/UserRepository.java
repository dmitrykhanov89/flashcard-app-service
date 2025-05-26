package com.sekhanov.flashcard.repository;

import com.sekhanov.flashcard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByLogin(String login);
}

