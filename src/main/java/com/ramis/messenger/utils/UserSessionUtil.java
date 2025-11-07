package com.ramis.messenger.utils;

import com.ramis.messenger.models.User;
import com.ramis.messenger.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class UserSessionUtil {

    private final UserRepository userRepository;

    public User checkIfUserInSession(HttpSession session, Principal principal) {
        User sessionUser = (User) session.getAttribute("user");

        if (sessionUser != null && sessionUser.getUsername().equals(principal.getName())) {
            return sessionUser;
        }

        User user = userRepository.getUserByUsername(principal.getName()).orElseThrow(() ->
                new EntityNotFoundException("User not found" + principal.getName()));
        session.setAttribute("user", user);
        return user;
    }
}
