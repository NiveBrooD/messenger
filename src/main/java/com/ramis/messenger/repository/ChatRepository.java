package com.ramis.messenger.repository;

import com.ramis.messenger.models.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat,Long> {


    @Query("select c from Chat c join fetch c.users where c.id = :id")
    Optional<Chat> getChatById(@Param("id") Long chatId);
}
