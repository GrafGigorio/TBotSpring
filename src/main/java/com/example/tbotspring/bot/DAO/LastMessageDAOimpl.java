package com.example.tbotspring.bot.DAO;

import com.example.tbotspring.bot.entity.Await;
import com.example.tbotspring.bot.entity.LastMessage;
import com.example.tbotspring.bot.entity.Store;
import com.example.tbotspring.bot.entity.UserBot;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import java.util.List;

public class LastMessageDAOimpl implements LastMessageDAO {
    SessionFactory sessionFactory = new Configuration()
            .configure()
            .addAnnotatedClass(LastMessage.class)
            .addAnnotatedClass(UserBot.class)
            .addAnnotatedClass(Store.class)
            .addAnnotatedClass(Await.class)
            .buildSessionFactory();

    @Override
    public LastMessage getLastMessage(Integer userId) {
        return this.getLastMessage(userId.longValue());
    }

    @Override
    public LastMessage getLastMessage(Long userId) {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            return session.createQuery("from LastMessage where userBot="+userId, LastMessage.class).getSingleResult();
        }
        catch (NoResultException d)
        {
            System.out.println("Last message not find >>>> "+d.getMessage());
            return null;
        }
        catch (NonUniqueResultException a)
        {
            //если найденно несколько последных сообщений удаляем все и создаем новое
            try(Session session = sessionFactory.getCurrentSession()) {
                session.beginTransaction();
                List<LastMessage> lastMessages = session.createQuery("from LastMessage where userBot="+userId, LastMessage.class).getResultList();

                for ( LastMessage lastMessage : lastMessages)
                {
                    session.delete(lastMessage);
                }
                session.getTransaction().commit();
                return this.getLastMessage(userId);
            }
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
