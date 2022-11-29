package com.example.tbotspring;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.data.relational.core.query.Criteria;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

public class dataBase {

    public static void saveUser(UserBot user)
    {
        SessionFactory sessionFactory = new Configuration()
                .configure()
                .addAnnotatedClass(UserBot.class)
                .addAnnotatedClass(Store.class)
                .buildSessionFactory();
        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
        }
        finally {
            sessionFactory.close();
        }
    }

    public static void updateUser(UserBot user)
    {
        SessionFactory sessionFactory = new Configuration()
                .configure()
                .addAnnotatedClass(UserBot.class)
                .addAnnotatedClass(Store.class)
                .buildSessionFactory();
        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();
        }
        finally {
            sessionFactory.close();
        }
    }
    public static UserBot getUser(long idTg)
    {
        SessionFactory sessionFactory = new Configuration()
                .configure()
                .addAnnotatedClass(UserBot.class)
                .addAnnotatedClass(Store.class)
                .addAnnotatedClass(Section.class)
                .buildSessionFactory();
        UserBot userBot = null;

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            System.out.println();
            userBot = session.byNaturalId(UserBot.class).using("tgId",idTg).load();

            System.out.println(userBot);
            session.getTransaction().commit();

        }
        finally {
            sessionFactory.close();
        }
        return userBot;
    }
    public static UserBot getUserID(long id)
    {
        SessionFactory sessionFactory = new Configuration()
                .configure()
                .addAnnotatedClass(UserBot.class)
                .addAnnotatedClass(Store.class)
                .addAnnotatedClass(Section.class)
                .buildSessionFactory();
        UserBot userBot = null;

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            userBot = session.get(UserBot.class,id);
            session.getTransaction().commit();
        }
        finally {
            sessionFactory.close();
        }
        return userBot;
    }
    public static List<Store> getUserStores(long idTg)
    {
        SessionFactory sessionFactory = new Configuration()
                .configure()
                .addAnnotatedClass(UserBot.class)
                .buildSessionFactory();
        List<Store> storeList = null;

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            storeList = session.createQuery("from Store " +
                            "where userid=" + idTg)
                    .getResultList();
            session.getTransaction().commit();

        }
        finally {
            sessionFactory.close();
        }

        if(storeList.size() > 0)
        {
            return storeList;
        }
        else return null;
    }
    public static Store saveStore(Store store)
    {
        try (SessionFactory sessionFactory = new Configuration()
                .configure()
                .addAnnotatedClass(Store.class)
                .buildSessionFactory()) {
            Session session = sessionFactory.openSession();

            session.beginTransaction();
            session.save(store);
            session.getTransaction().commit();
        }
        return store;
    }
    public static Store getStore(Long id)
    {
        Store store = null;
        try (SessionFactory sessionFactory = new Configuration()
                .configure()
                .addAnnotatedClass(Store.class)
                .buildSessionFactory()) {
            Session session = sessionFactory.openSession();

            session.beginTransaction();
            store = session.get(Store.class,id);
            session.getTransaction().commit();
        }
        return store;
    }
    public static Store deleteStore(Store store)
    {
        UserBot userBot = getUser(store.getUserid());
        List<Store> stores = userBot.getStores();
        if(stores.contains(store))
        {
            try (SessionFactory sessionFactory = new Configuration()
                    .configure()
                    .addAnnotatedClass(Store.class)
                    .buildSessionFactory()) {
                Session session = sessionFactory.openSession();

                session.beginTransaction();
                session.delete(store);
                session.getTransaction().commit();
            }
            catch (Exception d)
            {d.printStackTrace();}
            return store;
        }
        return null;
    }
    public static Store updateStore(Store store)
    {
        try (SessionFactory sessionFactory = new Configuration()
                .configure()
                .addAnnotatedClass(Store.class)
                .buildSessionFactory()) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            Store store1 = session.get(Store.class,store.getId());
            store1.setTitle(store.getTitle());

            session.getTransaction().commit();
        }
        catch (Exception d)
        {d.printStackTrace();}
        return store;
    }
}
