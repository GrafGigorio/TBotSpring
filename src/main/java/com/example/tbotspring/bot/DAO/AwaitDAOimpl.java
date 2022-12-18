package com.example.tbotspring.bot.DAO;

import com.example.tbotspring.bot.Var;
import com.example.tbotspring.bot.entity.Await;
import com.example.tbotspring.bot.entity.Store;
import com.example.tbotspring.bot.entity.UserBot;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class AwaitDAOimpl implements AwaitDao {

    SessionFactory sessionFactory;

    Session session;

    public AwaitDAOimpl() {
        this.sessionFactory = Var.sessionFactory;
        this.session = sessionFactory.openSession();
    }

    @Override
    public Await saveOrUpdate(Await await) {
        //Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            session.saveOrUpdate(await);
            session.getTransaction().commit();
            return session.get(Await.class,await.getId());
        }
        finally {
            session.close();
        }
    }
    @Override
    public Await set(Await await) {
        //Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            session.save(await);
            session.getTransaction().commit();
            return await;
        }
        finally {
            session.close();
        }
    }

    @Override
    public Await get(Long userid) {
        try {
            session.beginTransaction();
            return session.createQuery("from Await where userid="+userid, Await.class).getSingleResult();
        }
        finally {
            session.close();
        }
    }
    @Override
    public List<Await> getAll(Long userid) {
        //Session session = sessionFactory.getCurrentSession();
        try {
            //session.beginTransaction();
            session = sessionFactory.openSession();
            return session.createQuery("from Await where userid="+userid, Await.class).getResultList();
        }
        finally {
            session.close();
        }
    }

    @Override
    public Await delete(Await await) {
        //Session session = sessionFactory.getCurrentSession();
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.delete(await);
            session.getTransaction().commit();
            return await;
        }
        finally {
            session.close();
        }
    }
}
