package com.ramis.messenger.dao;

import com.ramis.messenger.models.Chat;
import com.ramis.messenger.models.Message;
import com.ramis.messenger.models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionCreator implements AutoCloseable {
    private final SessionFactory sessionFactory;

    public SessionCreator() {
        Configuration cfg = new Configuration();
        cfg.configure("hibernate.cfg.xml");
        cfg.addAnnotatedClass(Chat.class);
        cfg.addAnnotatedClass(Message.class);
        cfg.addAnnotatedClass(User.class);
        sessionFactory = cfg.buildSessionFactory();
    }

    public Session getSession() {
        return sessionFactory.openSession();
    }

    @Override
    public void close() {
        sessionFactory.close();
    }
}
