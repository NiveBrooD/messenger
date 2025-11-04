package com.ramis.messenger.controller;

import com.ramis.messenger.dto.ChatPreview;
import com.ramis.messenger.models.User;
import com.ramis.messenger.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final UserService userService;

    @GetMapping("/")
    public String home(Model model,
                       HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            Collection<ChatPreview> chatsPreview = userService.getChats(user);
            if (!chatsPreview.isEmpty()) {
                model.addAttribute("chats", chatsPreview);
            }
        } else {
            model.addAttribute("message", "Welcome! First of all u need to Login or Sign up!");
        }
        return "index";
    }

}
