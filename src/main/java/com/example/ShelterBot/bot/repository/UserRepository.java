package com.example.ShelterBot.bot.repository;


import com.example.ShelterBot.bot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 *
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(long id);

    Optional<User> findByChatId(long chatId);

}

