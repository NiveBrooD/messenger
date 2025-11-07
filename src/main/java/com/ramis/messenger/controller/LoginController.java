package com.ramis.messenger.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String registered, Model model) {
        if ("true".equals(registered)) {
            model.addAttribute("message", "You were successfully registered, please login.");
        }
        return "login";
    }


    //before spring-security
//    @PostMapping("/login")
//    public String login(@RequestParam String username,
//                        @RequestParam String password,
//                        HttpSession session,
//                        Model model) {
//        try {
//            User user = userService.getByUsernameAndPassword(username, password);
//            session.setAttribute("user", user);
//            return "redirect:/";
//        } catch (EntityNotFoundException e) {
//            model.addAttribute("error", e.getMessage());
//            return "login";
//        }
//    }
//
//    @PostMapping("/logout")
//    public String logout(HttpSession session) {
//        session.invalidate();
//        return "redirect:/login";
//    }
}
