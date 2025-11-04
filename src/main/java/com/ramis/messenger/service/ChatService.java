package com.ramis.messenger.service;

import com.ramis.messenger.models.Chat;
import com.ramis.messenger.models.Message;
import com.ramis.messenger.models.User;
import com.ramis.messenger.repository.ChatRepository;
import com.ramis.messenger.repository.MessageRepository;
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
    private final MessageRepository messageRepository;


    public Chat createChat(User user, String chatName) {
        Chat chat = new Chat();
        chat.setName(chatName);
        chat.setCreator(user);
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

    public List<Chat> getChatsByName(String chatName) {
        List<Chat> chats = chatRepository.findChatsByNameIgnoreCase(chatName).toList();
        if (chats.isEmpty()) {
            throw new EntityNotFoundException("Any chats with name " + chatName + " not found");
        }
        return chats;
    }

    public void delete(Long id) {
        if (id == null) {
            throw new RuntimeException("ID cannot be null");
        }
        chatRepository.deleteById(id);
    }
}
