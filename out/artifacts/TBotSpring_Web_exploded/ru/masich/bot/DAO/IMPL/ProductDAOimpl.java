package ru.masich.bot.DAO.IMPL;

import ru.masich.bot.DAO.interfaces.ProductDAO;
import ru.masich.bot.Var;
import ru.masich.bot.entity.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class ProductDAOimpl implements ProductDAO {
    SessionFactory sessionFactory;
    Session session;

    public ProductDAOimpl() {
        this.sessionFactory = Var.sessionFactory;
        this.session = sessionFactory.getCurrentSession();
    }

    @Override
    public Product get(int productId) {
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            return session.get(Product.class, productId);
        }
        finally {
            session.close();
        }
    }

    @Override
    public List<Product> getStore(Long store) {
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            return session.createQuery("from Product where shopId="+store, Product.class).getResultList();
        }
        finally {
            session.close();
        }
    }

    @Override
    public List<Product> getCatalog(Long catalogID) {
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            return session.createQuery("from Product where catalogId="+catalogID, Product.class).getResultList();
        }
        finally {
            session.close();
        }
    }

    @Override
    public void set(Product product) {
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(product);
            session.getTransaction().commit();
        }
        finally {
            session.close();
        }
    }

    @Override
    public void delete(int productId) {
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.delete(productId);
            session.getTransaction().commit();
        }
        finally {
            session.close();
        }
    }
}
