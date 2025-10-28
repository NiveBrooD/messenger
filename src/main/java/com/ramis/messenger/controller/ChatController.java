package com.ramis.messenger.controller;

import com.ramis.messenger.models.Chat;
import com.ramis.messenger.models.Message;
import com.ramis.messenger.models.User;
import com.ramis.messenger.service.ChatService;
import com.ramis.messenger.service.MessageService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {
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
            return "redirect:/chats";
        }
    }
}
