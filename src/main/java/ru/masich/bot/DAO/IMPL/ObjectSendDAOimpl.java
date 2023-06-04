package ru.masich.bot.DAO.IMPL;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.masich.bot.DAO.interfaces.ObjectSendDAO;
import ru.masich.bot.Var;
import ru.masich.bot.entity.ObjectSend;

public class ObjectSendDAOimpl implements ObjectSendDAO {
    SessionFactory sessionFactory;
    Session session;
    static Logger logger = LoggerFactory.getLogger(ObjectSendDAOimpl.class);

    public ObjectSendDAOimpl() {
        this.sessionFactory = Var.sessionFactory;
        this.session = sessionFactory.getCurrentSession();
    }

    @Override
    public ObjectSend getObject(Long objectId) {
        logger.info("<<  getObject objectId " + objectId);
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            return session.get(ObjectSend.class, objectId);
        }
        finally {
            session.close();
        }
    }

    @Override
    public ObjectSend updateObject(ObjectSend object) {
        logger.info("<<  updateObject object " + object);
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.saveOrUpdate(object);
            session.getTransaction().commit();
        }
        finally {
            session.close();
        }
        return object;
    }

    @Override
    public ObjectSend deleteObject(Long objectId) {
        logger.info("<<  deleteObject objectId " + objectId);
        ObjectSend o = null;
        try {
            session = sessionFactory.openSession();
            o = session.get(ObjectSend.class, objectId);
            session.beginTransaction();
            session.delete(o);
            session.getTransaction().commit();
        }
        finally {
            session.close();
        }
        return o;
    }
}
