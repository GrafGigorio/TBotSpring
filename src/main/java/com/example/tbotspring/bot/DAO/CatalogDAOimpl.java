package com.example.tbotspring.bot.DAO;

import com.example.tbotspring.bot.Var;
import com.example.tbotspring.bot.entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

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
    public void set(Catalog section) {
        try {
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
            session.beginTransaction();
            session.update(section);
            session.getTransaction().commit();
        }
        finally {
            session.close();
        }
    }
}
