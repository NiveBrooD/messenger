package com.ramis.messenger.repository;

import com.ramis.messenger.models.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("select m from Message m join fetch m.sender where m.chat.id = :chatId order by m.createdAt desc")
    Page<Message> getMessagesForChat(@Param("chatId") Long chatId, Pageable pageable);

}
