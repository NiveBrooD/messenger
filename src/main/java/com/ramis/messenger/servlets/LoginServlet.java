package com.ramis.messenger.servlets;

import com.ramis.messenger.dao.SessionCreator;
import com.ramis.messenger.dao.UserRepository;
import com.ramis.messenger.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserRepository userRepository = new UserRepository(new SessionCreator());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        User user = userRepository.getByUsernameAndPassword(username, password);
        if (user == null) {
            req.getSession().setAttribute("errorMessage", "Неверное имя пользователя или пароль");
            resp.sendRedirect("/login");
        } else {
            req.getSession().setAttribute("user", user);
            resp.sendRedirect("/");
        }
    }

}
