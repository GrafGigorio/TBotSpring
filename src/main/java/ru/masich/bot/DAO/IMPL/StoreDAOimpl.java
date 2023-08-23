package ru.masich.bot.DAO.IMPL;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.masich.bot.DAO.interfaces.StoreDao;
import ru.masich.bot.Var;
import ru.masich.bot.entity.Store;

import java.util.List;

public class StoreDAOimpl implements StoreDao {
    SessionFactory sessionFactory;
    Session session;
    static Logger logger = LoggerFactory.getLogger(StoreDAOimpl.class);

    public StoreDAOimpl() {
        this.sessionFactory = Var.sessionFactory;
        this.session = sessionFactory.getCurrentSession();
    }

    @Override
    public List<Store> getAllStore() {
        //Session session = sessionFactory.getCurrentSession();
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< getAllStore");
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            return session.createQuery("from Store", Store.class).getResultList();
        } finally {
            session.close();
        }
    }

    @Override
    public List<Store> getAllUserStores(Long userId) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< getAllUserStores userId " + userId);
        //Session session = sessionFactory.getCurrentSession();
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            return session.createQuery("from Store where userid=" + userId, Store.class).getResultList();
        } finally {
            session.close();
        }
    }

    @Override
    public Store getStore(Long storeId) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< getStore storeId " + storeId);
        //Session session = sessionFactory.getCurrentSession();
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            return session.get(Store.class, storeId);
        } finally {
            session.close();
        }
    }

    @Override
    public void saveOrUpdateStore(Store store) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<<  saveOrUpdateStore store " + store);
        //Session session = sessionFactory.getCurrentSession();
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.saveOrUpdate(store);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    @Override
    public void deleteStore(Store store) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< deleteStore store " + store);
        //Session session = sessionFactory.getCurrentSession();
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.delete(store);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }
}
