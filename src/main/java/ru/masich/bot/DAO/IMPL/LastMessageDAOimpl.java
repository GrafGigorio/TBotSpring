package ru.masich.bot.DAO.IMPL;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.masich.bot.DAO.interfaces.LastMessageDAO;
import ru.masich.bot.Var;
import ru.masich.bot.entity.LastMessage;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import java.util.List;

public class LastMessageDAOimpl implements LastMessageDAO {
    SessionFactory sessionFactory;
    Session session;
    static Logger logger = LoggerFactory.getLogger(LastMessageDAOimpl.class);

    public LastMessageDAOimpl() {
        this.sessionFactory = Var.sessionFactory;
        session = sessionFactory.getCurrentSession();
    }

    @Override
    public LastMessage getLastMessage(Integer userId) {
        return this.getLastMessage(userId.longValue());
    }

    @Override
    public LastMessage getLastMessage(Long userId) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<<  getLastMessage userId " + userId);
        try {
            session = session.isOpen() ? session : sessionFactory.openSession();
            session.beginTransaction();
            return session.createQuery("from LastMessage where userBot=" + userId, LastMessage.class).getSingleResult();
        } catch (NoResultException d) {
            System.out.println("Last message not find >>>> " + d.getMessage());
            return null;
        } catch (NonUniqueResultException a) {
            //если найденно несколько последных сообщений удаляем все и создаем новое
            session = session.isOpen() ? session : sessionFactory.openSession();
            session.beginTransaction();
            List<LastMessage> lastMessages = session.createQuery("from LastMessage where userBot=" + userId, LastMessage.class).getResultList();

            for (LastMessage lastMessage : lastMessages) {
                session.delete(lastMessage);
            }
            session.getTransaction().commit();
            return this.getLastMessage(userId);

        } finally {
            session.close();
        }
    }

    @Override
    public void setLastMessage(LastMessage lastMessage) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<<  setLastMessage lastMessage " + lastMessage);
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();

            session.save(lastMessage);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    @Override
    public void updateLastMessage(LastMessage lastMessage) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<<  updateLastMessage lastMessage " + lastMessage);
        //Session session = sessionFactory.getCurrentSession();
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();

            LastMessage lastMessage1 = session.get(LastMessage.class, lastMessage.getId());
            if (lastMessage1 != null) {
                lastMessage1.setLastMessageId(lastMessage.getLastMessageId());
                session.update(lastMessage1);
            } else {
                session.save(lastMessage);
            }
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    @Override
    public void deleteLastMessage(LastMessage lastMessage) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<<  deleteLastMessage lastMessage " + lastMessage);
        //Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            session.delete(lastMessage);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }
}
