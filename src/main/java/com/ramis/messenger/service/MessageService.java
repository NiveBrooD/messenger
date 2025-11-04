package com.ramis.messenger.service;

import com.ramis.messenger.models.Chat;
import com.ramis.messenger.models.Message;
import com.ramis.messenger.models.User;
import com.ramis.messenger.repository.ChatRepository;
import com.ramis.messenger.repository.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;

    public List<Message> getMessagesByChatId(Long chatId, int count) {
        PageRequest pageRequest = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Message> content = messageRepository.getMessagesForChat(chatId, pageRequest).getContent();
        List<Message> reversedContent = new ArrayList<>(content);
        Collections.reverse(reversedContent);
        return reversedContent;
    }

    public Message sendMessage(User sender, Long chatId, String text) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new EntityNotFoundException("Chat with id " + chatId + " not found")
        );
        Message message = Message.builder()
                .sender(sender)
                .chat(chat)
                .text(text).build();
        return messageRepository.save(message);
    }
}
