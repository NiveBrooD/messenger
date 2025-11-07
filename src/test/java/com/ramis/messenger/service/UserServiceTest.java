package com.ramis.messenger.service;

import com.ramis.messenger.dto.ChatPreview;
import com.ramis.messenger.dto.UserRegistrationTo;
import com.ramis.messenger.models.Chat;
import com.ramis.messenger.models.Message;
import com.ramis.messenger.models.Role;
import com.ramis.messenger.models.User;
import com.ramis.messenger.repository.ChatRepository;
import com.ramis.messenger.repository.MessageRepository;
import com.ramis.messenger.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void delete() {
        //given
        User user = new User();

        //when
        userService.delete(user);

        //then
        Mockito.verify(userRepository).delete(user);
    }

    @Test
    void delete_WhenEntityIsNull_ShouldThrowException() {
        //given
        doThrow(new IllegalArgumentException()).when(userRepository).delete(null);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> userService.delete(null));
    }

    @Test
    void getByUsernameAndPassword() {
        //given
        User build = User.builder()
                .username("username")
                .password("password")
                .build();
        when(userRepository.findByUsernameAndPassword(build.getUsername(), build.getPassword()))
                .thenReturn(Optional.of(build));

        //when
        User user = userService.getByUsernameAndPassword(build.getUsername(), build.getPassword());

        //then
        assertNotNull(user);
        assertEquals(build.getUsername(), user.getUsername());
        assertEquals(build.getPassword(), user.getPassword());
        verify(userRepository).findByUsernameAndPassword(build.getUsername(), build.getPassword());
    }

    @Test
    void getByUsernameAndPassword_WhenEntityNotFound_ShouldThrowException() {
        //given
        when(userRepository.findByUsernameAndPassword(anyString(), anyString())).thenReturn(Optional.empty());

        //when & then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> userService.getByUsernameAndPassword("RANDOMSTRING", "RANDOMSTRINGGG"));
        verify(userRepository).findByUsernameAndPassword(anyString(), anyString());
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void registerUser() {
        //given
        UserRegistrationTo dto = UserRegistrationTo.builder().username("username").password("password").build();
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(captor.capture())).thenAnswer(i -> {
            User user = i.getArgument(0);
            user.setId(3L);
            return user;
        });

        //when
        userService.registerUser(dto);
        User savedUser = captor.getValue();


        //then
        verify(userRepository).save(any());
        verify(userRepository).existsByUsername("username");
        assertEquals(dto.getUsername(), savedUser.getUsername());
        assertEquals(dto.getPassword(), savedUser.getPassword());
        assertEquals(3L, savedUser.getId());
    }

    @Test
    void registerUser_WhenEntityExistsByUsername_ShouldThrowException() {
        //given
        UserRegistrationTo alreadyExists = UserRegistrationTo.builder().username("already exists").build();
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        //when & then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.registerUser(alreadyExists));
        assertEquals("Username already exists: " + alreadyExists.getUsername(), ex.getMessage());
        verify(userRepository).existsByUsername(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser() {
        //given
        User given = User.builder().id(2L).username("oldUsername").password("oldPassword").build();
        when(userRepository.findById(2L)).thenAnswer(i -> Optional.of(User.builder()
                .id(2L)
                .username("oldUsername")
                .password("oldPassword")
                .build()));
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        //when
        User user = userService.updateUser(2L, "newUsername", "newPassword");

        //then
        verify(userRepository).findById(2L);
        verify(userRepository).existsByUsername("newUsername");
        verify(userRepository).save(any());

        assertEquals("newUsername", user.getUsername());
        assertEquals("newPassword", user.getPassword());
        assertEquals(2L, user.getId());
        assertNotEquals(given.getUsername(), user.getUsername());
        assertNotEquals(given.getPassword(), user.getPassword());
        assertNotSame(user, given);
    }

    @Test
    void updateUser_WhenEntityWasNotFound_ShouldThrowException() {
        //given
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        //when & then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> userService.updateUser(2L, "newUsername", "newPassword"));
        assertEquals("User not found", ex.getMessage());
        verify(userRepository).findById(any());
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_WhenEntityWithNewUsernameAlreadyExists_ShouldThrowException() {
        //given
        User user = User.builder().username("oldUsername").build();
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        //when & then
        RuntimeException runtimeException = assertThrows(RuntimeException.class,
                () -> userService.updateUser(2L, "newUsername", "newPassword"));
        assertEquals(
                String.format("Username with username: %s already exists", "newUsername"), runtimeException.getMessage()
        );
        verify(userRepository).findById(2L);
        verify(userRepository).existsByUsername("newUsername");
        verify(userRepository, never()).save(any());
    }

    @Test
    void addChatForUser() {
        //given
        User user = new User();
        user.setId(1L);
        Chat chat = new Chat();
        chat.setId(2L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(chatRepository.findById(2L)).thenReturn(Optional.of(chat));

        //when
        userService.addChatForUser(1L, 2L);

        //then
        verify(userRepository).findById(1L);
        verify(chatRepository).findById(2L);
        assertFalse(user.getChats().isEmpty());
        assertTrue(user.getChats().contains(chat));
    }

    @Test
    void addChatForUser_WhenUserEntityNotFound_ShouldThrowException() {
        //given
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        //when & then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> userService.addChatForUser(1L, 2L));
        assertEquals("User not found", ex.getMessage());
        verify(userRepository).findById(1L);
        verify(chatRepository, never()).findById(2L);
    }

    @Test
    void addChatForUser_WhenChatEntityNotFound_ShouldThrowException() {
        //given
        when(userRepository.findById(any())).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(chatRepository.findById(2L)).thenReturn(Optional.empty());

        //when & then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> userService.addChatForUser(1L, 2L));
        assertEquals("Chat not found", ex.getMessage());
        verify(userRepository).findById(1L);
        verify(chatRepository).findById(2L);
    }

    @Test
    void getChats() {
        //given
        Chat chat1 = new Chat();
        Chat chat2 = new Chat();
        chat1.setId(1L);
        chat1.setName("chat1");
        chat2.setId(2L);
        chat2.setName("chat2");
        User user = User.builder().id(1L).username("username").build();
        Message lastMessage = Message.builder().text("text").sender(User.builder().username("username").build()).build();
        when(userRepository.getUserChats(user.getId())).thenReturn(Stream.of(chat1, chat2));

        when(messageRepository.findLastMessageByChatId(chat1.getId())).thenReturn(Optional.of(lastMessage));
        when(messageRepository.findLastMessageByChatId(chat2.getId())).thenReturn(Optional.empty());

        //when
        Collection<ChatPreview> chatsPreviews = userService.getChats(user);

        //then
        assertEquals(2, chatsPreviews.size());
        chatsPreviews.forEach(chatPreview -> {
            if (chatPreview.getId() == 1L) {
                assertEquals(lastMessage.getText(), chatPreview.getLastMessage());
                assertEquals(lastMessage.getSender().getUsername(), chatPreview.getLastMessageSender());
            }
            if (chatPreview.getId() == 2L) {
                assertEquals("No messages yet", chatPreview.getLastMessage());
                assertEquals("", chatPreview.getLastMessageSender());
            }
        });
        verify(userRepository, times(1)).getUserChats(user.getId());
        verify(messageRepository, times(2)).findLastMessageByChatId(anyLong());
    }


    @Test
    void getByUsername_IsFound() {
        //given
        when(userRepository.getUserByUsername("username")).thenReturn(
                Optional.of(
                        User.builder().id(1L).username("username").build()));

        //when
        User user = userService.getByUsername("username");

        //then
        verify(userRepository).getUserByUsername("username");
        assertEquals("username", user.getUsername());
    }

    @Test
    void getByUsername_IsNotFound() {
        //given
        when(userRepository.getUserByUsername("username")).thenReturn(Optional.empty());

        //when & then
        Exception entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> userService.getByUsername("username")
        );
        assertEquals("User not found", entityNotFoundException.getMessage());
    }

    @Test
    void loadUserByUsername() {
        //given
        User user = User.builder()
                .username("username")
                .password("password")
                .role(Role.USER)
                .build();
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        //when
        UserDetails username = userService.loadUserByUsername("username");

        //then
        assertEquals("username", username.getUsername());
        assertEquals( "{noop}password", username.getPassword());
        assertEquals(1, username.getAuthorities().size());
        assertTrue(username.getAuthorities().contains(Role.USER));
        verify(userRepository).findByUsername("username");
    }
}