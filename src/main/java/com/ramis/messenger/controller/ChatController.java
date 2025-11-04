package com.ramis.messenger.controller;

import com.ramis.messenger.models.Chat;
import com.ramis.messenger.models.Message;
import com.ramis.messenger.models.User;
import com.ramis.messenger.service.ChatService;
import com.ramis.messenger.service.MessageService;
import com.ramis.messenger.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final UserService userService;
    private final ChatService chatService;
    private final MessageService messageService;

    @GetMapping("/chat/{chatId}")
    public String getChat(@PathVariable Long chatId,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        try {
            Chat chat = chatService.getChatWithUsers(chatId);
            List<Message> messages = messageService.getMessagesByChatId(chatId, 50);
            model.addAttribute("chat", chat);
            model.addAttribute("messages", messages);
            return "chat";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        }
    }

    @PostMapping("/chat/{chatId}")
    public String sendMessage(@PathVariable Long chatId,
                              @RequestParam String message,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "You need to be logged in");
            return "redirect:/";
        }
        try {
            messageService.sendMessage(user, chatId, message);

        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage()); //Bad
            return "redirect:/";
        }
        return  "redirect:/chat/" + chatId;
    }

    @GetMapping("/chat/create")
    public String createChat(Model model,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user") == null) {
            redirectAttributes.addFlashAttribute("error", "You need to be logged in");
            return "redirect:/";
        }

        return "chat_create";
    }

    @PostMapping("/chat/create")
    public String createChat(HttpSession session,
                             @RequestParam String chatName,
                             RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Please login first");
            return "redirect:/login";
        }
        try {
            Chat chat = chatService.createChat(user, chatName);
            redirectAttributes.addFlashAttribute("message", "Chat created");
            return "redirect:/chat/" + chat.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create chat");
            return "redirect:/";
        }
    }

    @PostMapping("/chat/join/{id}")
    public String joinToChat(HttpSession session,
                             @PathVariable Long id) {
        User user = (User) session.getAttribute("user");
        try {
            userService.addChatForUser(user.getId(), id);
        } catch (RuntimeException e) {
            //TODO: add redirectFlashAttribute("error") or smth else
            throw e;
        }
        return "redirect:/";
    }

    @PostMapping("/chat/search")
    public String findChat(@RequestParam String name,
                           RedirectAttributes redirectAttributes,
                           Model model,
                           HttpSession session) {
        if (session.getAttribute("user") == null) {
            redirectAttributes.addFlashAttribute("error", "You need to be logged in");
            return "redirect:/";
        }
        try {
            List<Chat> chats = chatService.getChatsByName(name);
            model.addAttribute("chats", chats);
            return "chat_search";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        }
    }

    @PostMapping("/chat/{id}/delete")
    public String deleteChat(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            chatService.delete(id);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        }
        redirectAttributes.addFlashAttribute("message", "Chat deleted");
        return "redirect:/";
    }
}
