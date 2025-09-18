package com.ramis.messenger.servlets;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebFilter({
        "/"
})
public class AuthentificationFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpSession session = req.getSession(true);
        Object user = (session != null) ? session.getAttribute("user") : null;
        if (user == null) {
            res.sendRedirect("/login");
        } else {
            chain.doFilter(req, res);
        }
    }
}
