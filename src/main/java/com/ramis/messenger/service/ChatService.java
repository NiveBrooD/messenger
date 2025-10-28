package com.ramis.messenger.service;

import com.ramis.messenger.models.Chat;
import com.ramis.messenger.models.User;
import com.ramis.messenger.repository.ChatRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;


    public Chat createChat(User user, String chatName) {
        Chat chat = new Chat();
        chat.setName(chatName.trim());
        chat.getUsers().add(user);
        return chatRepository.save(chat);
    }

    //TODO: make
    public List<Chat> getChats(User user) {
        return null;
    }

    /*
     *  Name can confuse, but it means that users for this chat is loaded.
     *  You can think that it's finding chats where List<User> not null, it's not.
     */
    public Chat getChatWithUsers(Long chatId) {
        return chatRepository.getChatById(chatId).orElseThrow(
                () -> new EntityNotFoundException("Chat with id " + chatId + " not found"));
    }
}
