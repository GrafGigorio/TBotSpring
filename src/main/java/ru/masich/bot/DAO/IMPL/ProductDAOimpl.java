package ru.masich.bot.DAO.IMPL;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.masich.bot.DAO.interfaces.ProductDAO;
import ru.masich.bot.Var;
import ru.masich.bot.entity.Product;

import java.util.List;

public class ProductDAOimpl implements ProductDAO {
    SessionFactory sessionFactory;
    Session session;
    static Logger logger = LoggerFactory.getLogger(ProductDAOimpl.class);

    public ProductDAOimpl() {
        this.sessionFactory = Var.sessionFactory;
        this.session = sessionFactory.getCurrentSession();
    }

    @Override
    public Product get(int productId) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<<  get productId " + productId);
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            return session.get(Product.class, productId);
        } finally {
            session.close();
        }
    }

    @Override
    public List<Product> getStore(Long store) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<<  getStore store " + store);
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            return session.createQuery("from Product where shopId=" + store, Product.class).getResultList();
        } finally {
            session.close();
        }
    }

    @Override
    public List<Product> getCatalog(Long catalogID) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<<  getCatalog catalogID " + catalogID);
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            return session.createQuery("from Product where catalogId=" + catalogID, Product.class).getResultList();
        } finally {
            session.close();
        }
    }

    @Override
    public void set(Product product) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<<  set product " + product);
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.saveOrUpdate(product);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    @Override
    public void delete(int productId) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<<  delete productId " + productId);
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.delete(productId);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }
}
