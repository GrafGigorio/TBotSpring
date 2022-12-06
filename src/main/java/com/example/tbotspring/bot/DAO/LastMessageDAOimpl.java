package com.example.tbotspring.bot.DAO;

import com.example.tbotspring.bot.entity.Await;
import com.example.tbotspring.bot.entity.LastMessage;
import com.example.tbotspring.bot.entity.Store;
import com.example.tbotspring.bot.entity.UserBot;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class LastMessageDAOimpl implements LastMessageDAO {
    SessionFactory sessionFactory = new Configuration()
            .configure()
            .addAnnotatedClass(LastMessage.class)
            .addAnnotatedClass(UserBot.class)
            .addAnnotatedClass(Store.class)
            .addAnnotatedClass(Await.class)
            .buildSessionFactory();

    @Override
    public LastMessage getLastMessage(Long userId) {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            return session.createQuery("from LastMessage where userBot.tgId="+userId, LastMessage.class).getSingleResult();
        }
    }

    @Override
    public void setLastMessage(LastMessage lastMessage) {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.save(lastMessage);
            session.getTransaction().commit();
        }
    }

    @Override
    public void updateLastMessage(LastMessage lastMessage) {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            LastMessage lastMessage1 = session.get(LastMessage.class, lastMessage.getId());
            lastMessage1.setLastMessageId(lastMessage.getLastMessageId());
            session.update(lastMessage1);
            session.getTransaction().commit();
        }
    }

    @Override
    public void deleteLastMessage(LastMessage lastMessage) {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.delete(lastMessage);
            session.getTransaction().commit();
        }
    }
}
