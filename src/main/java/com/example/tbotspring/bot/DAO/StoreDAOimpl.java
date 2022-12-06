package com.example.tbotspring.bot.DAO;

import com.example.tbotspring.bot.entity.Await;
import com.example.tbotspring.bot.entity.Store;
import com.example.tbotspring.bot.entity.UserBot;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class StoreDAOimpl implements StoreDao{
    SessionFactory sessionFactory = new Configuration()
            .configure()
            .addAnnotatedClass(UserBot.class)
            .addAnnotatedClass(Store.class)
            .addAnnotatedClass(Await.class)
            .buildSessionFactory();

    @Override
    public List<Store> getAllStore() {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            return session.createQuery("from Store", Store.class).getResultList();
        }
    }

    @Override
    public List<Store> getAllUserStores(Long userId) {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            return session.createQuery("from Store where userid="+userId, Store.class).getResultList();
        }
    }

    @Override
    public Store getStore(Long storeId) {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            return session.get(Store.class,storeId);
        }
    }

    @Override
    public Store saveOrUpdateStore(Store store) {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.saveOrUpdate(store);
            session.getTransaction().commit();
            return session.get(Store.class,store.getId());
        }
    }

    @Override
    public Store deleteStore(Store store) {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.delete(store);
            session.getTransaction().commit();
            return store;
        }
    }
}
