package ru.masich.bot.DAO.IMPL;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.masich.bot.DAO.interfaces.CatalogDAO;
import ru.masich.bot.Var;
import ru.masich.bot.entity.Catalog;
import ru.masich.bot.entity.Product;

import java.util.ArrayList;
import java.util.List;

public class CatalogDAOimpl implements CatalogDAO {
    SessionFactory sessionFactory;
    Session session;
    static Logger logger = LoggerFactory.getLogger(CatalogDAOimpl.class);

    public CatalogDAOimpl() {
        this.sessionFactory = Var.sessionFactory;
        this.session = sessionFactory.getCurrentSession();
    }

    @Override
    public List<Catalog> getCatalogAllStore(Long shopid) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< getCatalogAllStore shopid " + shopid);
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            return session.createQuery("from Catalog where shopId=" + shopid, Catalog.class).getResultList();
        } catch (javax.persistence.PersistenceException ea) {
            return new ArrayList<Catalog>();
        } finally {
            session.close();
        }
    }

    @Override
    public List<Catalog> getChildren(Long section) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< getChildren section " + section);
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            return session.createQuery("from Catalog where fatherId=" + section, Catalog.class).getResultList();
        } finally {
            session.close();
        }
    }

    @Override
    public Catalog get(Long catalogID) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< get Long catalogID " + catalogID);
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            return session.get(Catalog.class, catalogID);
        } finally {
            session.close();
        }
    }

    @Override
    public Catalog get(int catalogID) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< get int catalogID " + catalogID);
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            return session.get(Catalog.class, (long) catalogID);
        } finally {
            session.close();
        }
    }

    @Override
    public Catalog get(String catalogID) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< get String catalogID " + catalogID);
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();

            return session.get(Catalog.class, Long.valueOf(catalogID));
        } catch (Exception d) {
            System.out.println(catalogID);
            throw d;
        } finally {
            session.close();
        }
    }

    @Override
    public void set(Catalog section) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< set section " + section);
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(section);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    @Override
    public void delete(Catalog catalog) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< delete catalog " + catalog);
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            List<Product> dropProducts = session.createQuery("from Product where catalogId=" + catalog.getId(), Product.class).getResultList();
            for (Product product : dropProducts)
                session.delete(product);
            session.delete(catalog);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    @Override
    public void update(Catalog section) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< update section " + section);
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.update(section);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }
}
