package com.ramis.messenger.controller;

import com.ramis.messenger.dto.ChatPreview;
import com.ramis.messenger.models.User;
import com.ramis.messenger.service.UserService;
import com.ramis.messenger.utils.UserSessionUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Collection;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final UserService userService;
    private final UserSessionUtil userSessionUtil;

    @GetMapping("/")
    public String welcomePage() {
        return "index";
    }


    @GetMapping("/chats")
    public String home(Model model,
                       HttpSession session,
                       Principal principal) {
        User user;
        try {
            user = userSessionUtil.checkIfUserInSession(session, principal);

        } catch (EntityNotFoundException e) {
            return "redirect:/logout";
        }
        Collection<ChatPreview> chatsPreview = userService.getChats(user);
        if (!chatsPreview.isEmpty()) {
            model.addAttribute("chats", chatsPreview);
        }

        return "chats";
    }

}
