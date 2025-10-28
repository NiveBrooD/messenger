package com.ramis.messenger.controller;

import com.ramis.messenger.dto.UserRegistrationTo;
import com.ramis.messenger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @GetMapping("/register")
    public String registration(Model model) {
        model.addAttribute("user", new UserRegistrationTo());
        return "register";
    }

    @PostMapping("/register")
    public String registration(@ModelAttribute("user") UserRegistrationTo userRegistrationTo,
                               Model model) {
        try {
            userService.registerUser(userRegistrationTo);
            return  "redirect:/login?registered=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }

    }
}
