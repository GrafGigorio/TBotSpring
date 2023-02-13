package ru.masich.bot.DAO.IMPL;

import ru.masich.bot.DAO.interfaces.StoreDao;
import ru.masich.bot.Var;
import ru.masich.bot.entity.Store;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class StoreDAOimpl implements StoreDao {
    SessionFactory sessionFactory;
    Session session;

    public StoreDAOimpl() {
        this.sessionFactory = Var.sessionFactory;
        this.session = sessionFactory.getCurrentSession();
    }

    @Override
    public List<Store> getAllStore() {
        //Session session = sessionFactory.getCurrentSession();
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            return session.createQuery("from Store", Store.class).getResultList();
        }
        finally {
            session.close();
        }
    }

    @Override
    public List<Store> getAllUserStores(Long userId) {
        //Session session = sessionFactory.getCurrentSession();
        try{
            session = sessionFactory.openSession();
            session.beginTransaction();
            return session.createQuery("from Store where userid="+userId, Store.class).getResultList();
        }
        finally {
            session.close();
        }
    }

    @Override
    public Store getStore(Long storeId) {
        //Session session = sessionFactory.getCurrentSession();
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            return session.get(Store.class,storeId);
        }
        finally {
            session.close();
        }
    }

    @Override
    public Store saveOrUpdateStore(Store store) {
        //Session session = sessionFactory.getCurrentSession();
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.saveOrUpdate(store);
            session.getTransaction().commit();
            return store;
        }
        finally {
            session.close();
        }
    }

    @Override
    public Store deleteStore(Store store) {
        //Session session = sessionFactory.getCurrentSession();
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.delete(store);
            session.getTransaction().commit();
            return store;
        }
        finally {
            session.close();
        }
    }
}
