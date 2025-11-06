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
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;

    @Transactional
    public Chat createChat(User user, String chatName) {
        if (user == null) {
            throw new IllegalArgumentException("User can't be null");
        }
        Chat chat = new Chat();
        chat.setName(chatName);
        chat.setCreator(user);
        chat.getUsers().add(user);
        return chatRepository.save(chat);
    }

    /*
     *  Name can confuse, but it means that users for this chat is loaded.
     *  You can think that it's finding chats where List<User> not null, it's not.
     */
    public Chat getChatWithUsers(Long chatId) {
        if (chatId == null || chatId < 1) {
            throw new IllegalArgumentException("Chat id can't be null or less than 1");
        }
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

    @Transactional
    public void delete(Long id) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("ID cannot be null or less than 1");
        }
        chatRepository.deleteById(id);
    }
}
