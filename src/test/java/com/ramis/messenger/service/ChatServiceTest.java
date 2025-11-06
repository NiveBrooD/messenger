package com.ramis.messenger.service;

import com.ramis.messenger.models.Chat;
import com.ramis.messenger.models.User;
import com.ramis.messenger.repository.ChatRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private ChatService chatService;

    @Test
    void createChat() {
        //Given
        User user = new User();
        when(chatRepository.save(any(Chat.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        //When
        Chat chat = chatService.createChat(user, "test");

        //Then
        assertNotNull(chat);
        verify(chatRepository, Mockito.times(1)).save(chat);
        assertEquals("test", chat.getName());
        assertEquals(user, chat.getCreator());
        assertTrue(chat.getUsers().contains(user));
        assertEquals(1, chat.getUsers().size());
    }

    @Test
    void createChat_WithNullUser_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> chatService.createChat(null, "test")
        );
    }

    @Test
    void getChatWithUsers() {
        //Given
        Chat chat = new Chat();
        chat.setId(2L);
        chat.getUsers().add(new User());
        when(chatRepository.getChatById(2L)).thenReturn(Optional.of(chat));

        //when
        Chat chatWithUsers = chatService.getChatWithUsers(2L);

        //then
        assertNotNull(chatWithUsers);
        verify(chatRepository, Mockito.times(1)).getChatById(2L);
        assertFalse(chatWithUsers.getUsers().isEmpty());
    }

    @Test
    void getChatWithUsers_WithNullId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> chatService.getChatWithUsers(null));
        assertThrows(IllegalArgumentException.class,
                () -> chatService.getChatWithUsers(0L));
    }

    @Test
    void getChatWithUsers_IfChatNotFound_ShouldThrowException() {
        //given
        when(chatRepository.getChatById(1L)).thenReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class,
                () -> chatService.getChatWithUsers(1L));
    }

    @Test
    void getChatsByName() {
        //given
        String chatName = "test";
        when(chatRepository.findChatsByNameIgnoreCase(chatName)).thenAnswer(invocation -> {
            //излишне сложно, но я оставлю чтобы не забыть. в первый раз так сделал всё-таки :)
            Chat chat = new Chat();
            Chat chat1 = new Chat();
            chat.setName(invocation.getArgument(0));
            chat1.setName(invocation.getArgument(0));
            return Stream.of(chat, chat1);
        });

        //when
        List<Chat> chats = chatService.getChatsByName(chatName);

        //then
        verify(chatRepository, Mockito.times(1)).findChatsByNameIgnoreCase(chatName);
        assertNotNull(chats);
        assertEquals(2, chats.size());
        assertEquals("test", chats.get(0).getName());
        assertEquals("test", chats.get(1).getName());
    }

    @Test
    void getChatsByName_WhenChatsWasNotFound_ShouldThrowException() {
        //given
        when(chatRepository.findChatsByNameIgnoreCase("fgsdgdgd")).thenReturn(Stream.empty());

        //when & then
        assertThrows(EntityNotFoundException.class,
                () -> chatService.getChatsByName("fgsdgdgd"));
    }

    @Test
    void delete() {
        //when
        chatService.delete(1L);

        //then
        verify(chatRepository).deleteById(1L);

    }

    @Test
    void deleteById_WithNullId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> chatService.delete(null));
    }

    @Test
    void deleteById_WithNegativeId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> chatService.delete(-3L));
    }
}