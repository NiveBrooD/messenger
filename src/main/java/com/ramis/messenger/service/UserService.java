package com.ramis.messenger.service;

import com.ramis.messenger.dto.ChatPreview;
import com.ramis.messenger.models.Chat;
import com.ramis.messenger.models.Message;
import com.ramis.messenger.repository.ChatRepository;
import com.ramis.messenger.repository.MessageRepository;
import com.ramis.messenger.repository.UserRepository;
import com.ramis.messenger.dto.UserRegistrationTo;
import com.ramis.messenger.models.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;

    
    public Collection<User> getAll() {
        return userRepository.findAll();
    }

    
    public User get(Long id) {
        return userRepository.getReferenceById(id);
    }

    
    public User save(User user) {
        return userRepository.save(user);
    }

    
    public User updateUsernameAndPass(User user, Long userId) {
        User userDB = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (Objects.nonNull(userDB.getUsername()) && StringUtils.hasText(user.getUsername())) {
            if (!userDB.getUsername().equals(user.getUsername())) {
                if (!userRepository.existsByUsername(user.getUsername())) {
                    userDB.setUsername(user.getUsername());
                } else {
                    throw new RuntimeException("Username already exists");
                }
            }
        }

        if ((user.getPassword() != null) && StringUtils.hasText(user.getPassword())) {
            userDB.setPassword(user.getPassword());
        }
        return userRepository.save(userDB);
    }

    
    public void delete(User user) {
        userRepository.delete(user);
    }

    
    public User getByUsernameAndPassword(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );
    }

    
    public List<User> saveAll(Iterable<User> entities) {
        return userRepository.saveAll(entities);
    }

    
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

    public User updateUser(Long id, String newUsername, String newPassword) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (!user.getUsername().equals(newUsername) && userRepository.existsByUsername(newUsername)) {
            throw new RuntimeException("Username already exists: " + newUsername);
        }
        user.setUsername(newUsername);
        user.setPassword(newPassword);
        return userRepository.save(user);
    }

    public void addChatForUser(Long userId, Long chatId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new EntityNotFoundException("Chat not found"));
        user.getChats().add(chat);
    }

    public Collection<ChatPreview> getChats(User user) {
        List<Chat> chatsForUser = userRepository.getUserChats(user.getId());
        return chatsForUser.stream()
                .map(this::toChatPreview)
                .toList();
    }

    private ChatPreview toChatPreview(Chat chat) {
        List<Message> content = messageRepository
                .getMessagesForChat(
                        chat.getId(),
                        PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt")))
                .getContent();
        String lastMessage;
        String lastMessageSender;
        try {
            Message message = content.get(0);
            lastMessage = message.getText();
            lastMessageSender = message.getSender().getUsername();
        } catch (IndexOutOfBoundsException e) {
            lastMessage = "";
            lastMessageSender = "";
        }
        return new ChatPreview(chat.getId(), chat.getName(), lastMessage,lastMessageSender);
    }

}
