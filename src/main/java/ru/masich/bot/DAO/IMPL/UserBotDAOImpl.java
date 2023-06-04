package ru.masich.bot.DAO.IMPL;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.masich.bot.DAO.interfaces.UserBotDAO;
import ru.masich.bot.Var;
import ru.masich.bot.entity.UserBot;

import java.util.List;

@Repository
public class UserBotDAOImpl implements UserBotDAO {
//    @Autowired
//    SessionFactory sessionFactory;

    SessionFactory sessionFactory;
    static Logger logger = LoggerFactory.getLogger(UserBotDAOImpl.class);

    private Session getSession() {
        if (sessionFactory == null)
            sessionFactory = Var.getSessionFactory();
        return sessionFactory.openSession();
    }

    @Override
    public List<UserBot> getAllUserBot() {
        logger.info("<<  getAllUserBot");
        try (Session session = getSession()) {

            session.beginTransaction();
            return session.createQuery("from UserBot", UserBot.class).getResultList();
        }
    }

    @Override
    public UserBot getUserBot(Long id) {
        logger.info("<<  getUserBot Long " + id);
        try (Session session = getSession()) {
            session.beginTransaction();
            return session.get(UserBot.class, id);
        }
    }

    @Override
    public UserBot getUserBot(User user) {
        logger.info("<<  getUserBot User " + user);
        try (Session session = getSession()) {
            session.beginTransaction();
            return session.byNaturalId(UserBot.class).using("tgId", user.getId()).load();
            //return session.createQuery("from UserBot where tgId="+ user.getId(), UserBot.class).getSingleResult();
        }

    }

    @Override
    public UserBot update(UserBot userBot) {
        logger.info("<<  UserBot userBot " + userBot);
        try (Session session = getSession()) {
            session.beginTransaction();
            session.update(userBot);
            session.getTransaction().commit();
            return userBot;
        }

    }

    @Override
    public UserBot save(UserBot userBot) {
        logger.info("<<  save userBot " + userBot);
        try (Session session = getSession()) {
            session.beginTransaction();
            session.save(userBot);
            session.getTransaction().commit();
            return userBot;
        }

    }

    @Override
    public UserBot deleteUserBot(UserBot userBot) {
        logger.info("<<  deleteUserBot userBot " + userBot);
        try (Session session = getSession()) {
            session.beginTransaction();
            session.delete(userBot);
            session.getTransaction().commit();
        }
        return userBot;
    }
}
