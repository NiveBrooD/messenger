package com.ramis.messenger.controller;

import com.ramis.messenger.models.User;
import com.ramis.messenger.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService;

    @GetMapping("/profile")
    public String getInfo() {
        return "profile";
    }

    @PostMapping("/profile")
    public String changeUserData(@RequestParam String username,
                                 @RequestParam String password,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        try {
            User updatedUser = userService.updateUser(user.getId(), username, password);
            session.setAttribute("user", updatedUser);
            redirectAttributes.addFlashAttribute("success", "User updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/profile/delete")
    public String deleteUser(HttpSession session,
                             @RequestParam String confirmation,
                             RedirectAttributes  redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (!"Delete".equals(confirmation)) {
            redirectAttributes.addFlashAttribute("error", "Please confirm your deletion");
            return "redirect:/profile";
        }
        try {
            userService.delete(user);
            session.invalidate();
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile";
        }
    }
}
