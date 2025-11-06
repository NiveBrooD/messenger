package com.ramis.messenger.service;

import com.ramis.messenger.dto.ChatPreview;
import com.ramis.messenger.dto.UserRegistrationTo;
import com.ramis.messenger.models.Chat;
import com.ramis.messenger.models.Message;
import com.ramis.messenger.models.User;
import com.ramis.messenger.repository.ChatRepository;
import com.ramis.messenger.repository.MessageRepository;
import com.ramis.messenger.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;

    @Transactional
    public void delete(User user) {
        userRepository.delete(user);
    }


    public User getByUsernameAndPassword(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );
    }



    @Transactional
    public void registerUser(UserRegistrationTo userRegistrationTo) {
        if (userRepository.existsByUsername(userRegistrationTo.getUsername())) {
            throw new RuntimeException("Username already exists: " + userRegistrationTo.getUsername());
        }
        User user = User.builder()
                .username(userRegistrationTo.getUsername())
                .password(userRegistrationTo.getPassword())
                .build();
        userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, String newUsername, String newPassword) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (!user.getUsername().equals(newUsername) && userRepository.existsByUsername(newUsername)) {
            throw new RuntimeException(String.format("Username with username: %s already exists", newUsername));
        }
        user.setUsername(newUsername);
        user.setPassword(newPassword);
        return userRepository.save(user);
    }

    @Transactional
    public void addChatForUser(Long userId, Long chatId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new EntityNotFoundException("Chat not found"));
        user.getChats().add(chat);
    }

    public Collection<ChatPreview> getChats(User user) {
        Stream<Chat> chatsForUser = userRepository.getUserChats(user.getId());
        return chatsForUser.map(this::toChatPreview)
                .toList();
    }

    private ChatPreview toChatPreview(Chat chat) {
        String lastMessageSender;
        String lastMessageText;
        try {
            Message lastMessage = messageRepository.findLastMessageByChatId(chat.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Message not found"));
            lastMessageText = lastMessage.getText();
            lastMessageSender = lastMessage.getSender().getUsername();
        } catch (IndexOutOfBoundsException | EntityNotFoundException e) {
            lastMessageText = "No messages yet";
            lastMessageSender = "";
        }
        return new ChatPreview(chat.getId(), chat.getName(), lastMessageText, lastMessageSender);
    }
}
