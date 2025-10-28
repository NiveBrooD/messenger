package com.ramis.messenger.service;

import com.ramis.messenger.models.Message;
import com.ramis.messenger.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public List<Message> getMessagesByChatId(Long chatId, int count) {
        PageRequest pageRequest = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Message> content = messageRepository.getMessagesForChat(chatId, pageRequest).getContent();
        Collections.reverse(content);
        return content;
    }

}
