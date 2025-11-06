package com.ramis.messenger.service;

import com.ramis.messenger.models.Chat;
import com.ramis.messenger.models.Message;
import com.ramis.messenger.models.User;
import com.ramis.messenger.repository.ChatRepository;
import com.ramis.messenger.repository.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.testcontainers.shaded.com.google.common.collect.Lists;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private MessageService messageService;

    @Test
    void getMessagesByChatId() {
        //given
        int count = 4;
        List<Message> given = Arrays.asList(
                new Message(1L, "message1", new User(), new Chat(), LocalDateTime.now().plusSeconds(1)),
                new Message(2L, "message2", new User(), new Chat(), LocalDateTime.now().plusSeconds(2)),
                new Message(3L, "message3", new User(), new Chat(), LocalDateTime.now().plusSeconds(3)),
                new Message(4L, "message4", new User(), new Chat(), LocalDateTime.now().plusSeconds(4)),
                new Message(5L, "message5", new User(), new Chat(), LocalDateTime.now().plusSeconds(5))
        );
        List<Message> afterCount = given.subList(0, count);
        Page<Message> page = new PageImpl<>(afterCount);
        when(messageRepository.getMessagesForChat(any(), Mockito.any())).thenReturn(page);

        //when
        List<Message> messages = messageService.getMessagesByChatId(1L, count);

        //then
        Mockito.verify(messageRepository).getMessagesForChat(any(), Mockito.any());
        assertEquals(4, messages.size());
        assertEquals(afterCount.get(0).getText(), messages.get(messages.size() - 1).getText());
        assertTrue(given.size() > messages.size());
        assertTrue(given.containsAll(messages));
        assertEquals(afterCount, Lists.reverse(messages));
    }

    @Test
    void sendMessage() {
        //given
        Chat chat = new Chat();
        User user = new User();
        user.setUsername("username");
        when(chatRepository.findById(any())).thenReturn(Optional.of(chat));
        when(messageRepository.save(any(Message.class))).thenAnswer(i -> {
            Message message = i.getArgument(0);
            message.setId(10L);
            return message;
        });

        //when
        Message message = messageService.sendMessage(user, 1L, "test");

        //then
        verify(chatRepository).findById(any());
        verify(messageRepository).save(any(Message.class));
        assertEquals(user, message.getSender());
        assertEquals("test", message.getText());
        assertEquals(chat, message.getChat());
        assertEquals(10L, message.getId());
    }

    @Test
    void sendMessage_WhenUserIsNull_ShouldThrowException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> messageService.sendMessage(null, 1L, "test"));
        assertEquals("Sender can't be null", ex.getMessage());
        verify(chatRepository, never()).findById(any());
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void sendMessage_WhenChatNotFound_ShouldThrowException() {
        //given
        when(chatRepository.findById(any())).thenReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class,
                () -> messageService.sendMessage(new User(), 1L, "test"));
        verify(chatRepository).findById(any());
        verify(messageRepository, never()).save(any(Message.class));
    }
}