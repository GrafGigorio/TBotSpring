package ru.masich.bot.DAO.IMPL;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.masich.bot.DAO.interfaces.AwaitDao;
import ru.masich.bot.Var;
import ru.masich.bot.entity.Await;

import java.util.List;

public class AwaitDAOimpl implements AwaitDao {

    SessionFactory sessionFactory;
    Session session;
    static Logger logger = LoggerFactory.getLogger(AwaitDAOimpl.class);

    public AwaitDAOimpl() {
        this.sessionFactory = Var.sessionFactory;
        this.session = sessionFactory.openSession();
    }

    @Override
    public Await saveOrUpdate(Await await) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<<  saveOrUpdate await "+ await);
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
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<<  set await " + await);
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
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<<  get userid " + userid);
        try {
            session.beginTransaction();
            return session.createQuery("from Await where userid="+userid, Await.class).getSingleResult();
        }
        catch (javax.persistence.NoResultException e)
        {
            System.out.println(e);
            return null;
        }
        finally {
            session.close();
        }
    }
    @Override
    public List<Await> getAll(Long userid) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<<  getAll Await userid " + userid);
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
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<<  delete");
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
