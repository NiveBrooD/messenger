package com.ramis.messenger.dao;

import com.ramis.messenger.models.User;
import jakarta.persistence.NoResultException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Collection;
import java.util.List;

public class UserRepository implements Repository<User>{
    private final SessionCreator sessionCreator;

    public UserRepository(SessionCreator sessionCreator) {
        this.sessionCreator = sessionCreator;
    }

    @Override
    public Collection<User> getAll() {
        Session session = sessionCreator.getSession();
        Transaction transaction = session.beginTransaction();
        try (session) {
            List<User> users = session.createQuery("SELECT u FROM User u", User.class).getResultList();
            transaction.commit();
            return users;
        } catch (Exception ex) {
            transaction.rollback();
            throw new RuntimeException(ex);
        }
    }

    @Override
    public User get(Long id) {
        Session session = sessionCreator.getSession();
        Transaction transaction = session.beginTransaction();
        try (session) {
            User user = session.get(User.class, id);
            transaction.commit();
            return user;
        } catch (Exception ex) {
            transaction.rollback();
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void create(User entity) {
        Session session = sessionCreator.getSession();
        Transaction transaction = session.beginTransaction();
        try (session) {
            session.persist(entity);
            transaction.commit();
        } catch (Exception ex) {
            transaction.rollback();
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(User entity) {
        Session session = sessionCreator.getSession();
        Transaction transaction = session.beginTransaction();
        try (session) {
            session.merge(entity);
            transaction.commit();
        } catch (Exception ex) {
            transaction.rollback();
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void delete(User entity) {
        Session session = sessionCreator.getSession();
        Transaction transaction = session.beginTransaction();
        try (session) {
            session.remove(entity);
            transaction.commit();
        }  catch (Exception ex) {
            transaction.rollback();
            throw new RuntimeException(ex);
        }
    }

    public User getByUsernameAndPassword(String username, String password) {
        Session session = sessionCreator.getSession();
        Transaction transaction = session.beginTransaction();
        try (session) {
            Query<User> query = session.createQuery("select u from User u where u.username = :username and u.password = :password", User.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            User user = null;
            try {
                 user = query.getSingleResult();
            }  catch (NoResultException ignored){

            }
//            System.out.println(user instanceof HibernateProxy);
            transaction.commit();
            return user;
        } catch (Exception ex) {
            transaction.rollback();
            throw new RuntimeException(ex);
        }
    }
}
