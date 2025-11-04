package com.ramis.messenger.repository;

import com.ramis.messenger.models.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface ChatRepository extends JpaRepository<Chat,Long> {


    @Query("select c from Chat c join fetch c.users left join fetch c.creator where c.id = :id")
    Optional<Chat> getChatById(@Param("id") Long chatId);

    @Query("select c from Chat c join fetch c.messages where c.id = :id")
    Optional<Chat> getChatByIdFetchMessages(@Param("id") Long chatId);

    Stream<Chat> findChatsByNameIgnoreCase(@Param("name") String name);
}
