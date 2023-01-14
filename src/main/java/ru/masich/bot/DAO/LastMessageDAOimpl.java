package ru.masich.bot.DAO;

import ru.masich.bot.Var;
import ru.masich.bot.entity.LastMessage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import java.util.List;

public class LastMessageDAOimpl implements LastMessageDAO {
    SessionFactory sessionFactory;
    Session session;

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

        try {
            session = session.isOpen() ? session : sessionFactory.openSession();
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
            session = session.isOpen() ? session : sessionFactory.openSession();
            session.beginTransaction();
            List<LastMessage> lastMessages = session.createQuery("from LastMessage where userBot="+userId, LastMessage.class).getResultList();

            for ( LastMessage lastMessage : lastMessages)
            {
                session.delete(lastMessage);
            }
            session.getTransaction().commit();
            return this.getLastMessage(userId);

        }
        finally {
            session.close();
        }
    }

    @Override
    public void setLastMessage(LastMessage lastMessage) {
        try {
            session.beginTransaction();
            session.save(lastMessage);
            session.getTransaction().commit();
        }
        finally {
            session.close();
        }
    }

    @Override
    public void updateLastMessage(LastMessage lastMessage) {
        //Session session = sessionFactory.getCurrentSession();
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            LastMessage lastMessage1 = session.get(LastMessage.class, lastMessage.getId());
            lastMessage1.setLastMessageId(lastMessage.getLastMessageId());
            session.update(lastMessage1);
            session.getTransaction().commit();
        }
        finally {
            session.close();
        }
    }

    @Override
    public void deleteLastMessage(LastMessage lastMessage) {
        //Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            session.delete(lastMessage);
            session.getTransaction().commit();
        }
        finally {
            session.close();
        }
    }
}
