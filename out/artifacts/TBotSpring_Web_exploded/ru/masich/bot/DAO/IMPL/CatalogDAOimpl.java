package ru.masich.bot.DAO.IMPL;

import ru.masich.bot.DAO.interfaces.CatalogDAO;
import ru.masich.bot.Var;
import ru.masich.bot.entity.Catalog;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

public class CatalogDAOimpl implements CatalogDAO {
    SessionFactory sessionFactory;
    Session session;
    public CatalogDAOimpl() {
        this.sessionFactory = Var.sessionFactory;
        this.session = sessionFactory.getCurrentSession();
    }

    @Override
    public List<Catalog> getCatalogAllStore(Long shopid) {
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            return session.createQuery("from Catalog where shopId="+shopid, Catalog.class).getResultList();
        }
        catch (javax.persistence.PersistenceException ea)
        {
            return new ArrayList<Catalog>();
        }
        finally {
            session.close();
        }
    }

    @Override
    public List<Catalog> getChildren(Long section) {
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            return session.createQuery("from Catalog where fatherId="+section, Catalog.class).getResultList();
        }
        finally {
            session.close();
        }
    }

    @Override
    public Catalog get(Long catalogID) {
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            return session.get(Catalog.class, catalogID);
        }
        finally {
            session.close();
        }
    }
    @Override
    public Catalog get(int catalogID) {
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            return session.get(Catalog.class, catalogID);
        }
        finally {
            session.close();
        }
    }
    @Override
    public Catalog get(String catalogID) {
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();

            return session.get(Catalog.class, Long.valueOf(catalogID));
        }
        catch (Exception d)
        {
            System.out.println(catalogID);
            throw d;
        }
        finally {
            session.close();
        }
    }

    @Override
    public void set(Catalog section) {
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(section);
            session.getTransaction().commit();
        }
        finally {
            session.close();
        }
    }

    @Override
    public void delete(Catalog section) {
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.delete(section);
            session.getTransaction().commit();
        }
        finally {
            session.close();
        }
    }

    @Override
    public void update(Catalog section) {
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.update(section);
            session.getTransaction().commit();
        }
        finally {
            session.close();
        }
    }
}
