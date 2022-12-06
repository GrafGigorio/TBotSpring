package com.example.tbotspring.bot.DAO;

import com.example.tbotspring.bot.entity.Await;
import com.example.tbotspring.bot.entity.Store;
import com.example.tbotspring.bot.entity.UserBot;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class AwaitDAOimpl implements AwaitDao {

    SessionFactory sessionFactory = new Configuration()
            .configure()
            .addAnnotatedClass(UserBot.class)
            .addAnnotatedClass(Store.class)
            .addAnnotatedClass(Await.class)
            .buildSessionFactory();

    @Override
    public Await saveOrUpdate(Await await) {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.saveOrUpdate(await);
            session.getTransaction().commit();
            return session.get(Await.class,await.getId());
        }
    }

    @Override
    public Await get(Long userid) {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            return session.createQuery("from Await where userid="+userid, Await.class).getSingleResult();
        }
    }
    @Override
    public List<Await> getAll(Long userid) {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            return session.createQuery("from Await where userid="+userid, Await.class).getResultList();
        }
    }

    @Override
    public Await delete(Await await) {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.delete(await);
            session.getTransaction().commit();
            return await;
        }
    }
}
