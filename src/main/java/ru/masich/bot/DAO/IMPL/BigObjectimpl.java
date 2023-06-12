package ru.masich.bot.DAO.IMPL;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.masich.bot.DAO.interfaces.BigObjectDAO;
import ru.masich.bot.Var;
import ru.masich.bot.entity.BIgObject;

public class BigObjectimpl implements BigObjectDAO {
    SessionFactory sessionFactory;
    Session session;
    static Logger logger = LoggerFactory.getLogger(CatalogDAOimpl.class);
    public BigObjectimpl() {
        this.sessionFactory = Var.sessionFactory;
        this.session = sessionFactory.getCurrentSession();
    }
    @Override
    public BIgObject get(int id) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< get id " + id);
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            return session.get(BIgObject.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public void save(int userId, String data) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< save userId " + userId);
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(new BIgObject(userId, data));
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    @Override
    public void save(BIgObject object) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< save object " + object);
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(object);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }
}
