package com.ramis.messenger.repository;

import com.ramis.messenger.models.Chat;
import com.ramis.messenger.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameAndPassword(String username, String password);

    boolean existsByUsername(String username);

    @Query("select u.chats from User u where u.id = :userId")
    List<Chat> getUserChats(@Param("userId") Long userId);
}
