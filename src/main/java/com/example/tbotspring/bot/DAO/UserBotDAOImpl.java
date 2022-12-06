package com.example.tbotspring.bot.DAO;

import com.example.tbotspring.bot.entity.Await;
import com.example.tbotspring.bot.entity.LastMessage;
import com.example.tbotspring.bot.entity.Store;
import com.example.tbotspring.bot.entity.UserBot;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Repository;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
@Repository
public class UserBotDAOImpl implements UserBotDAO{
//    @Autowired
//    SessionFactory sessionFactory;
    SessionFactory sessionFactory = new Configuration()
            .configure()
            .addAnnotatedClass(UserBot.class)
            .addAnnotatedClass(Store.class)
            .addAnnotatedClass(Await.class)
            .addAnnotatedClass(LastMessage.class)
            .buildSessionFactory();
    @Override
    public List<UserBot> getAllUserBot() {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            return session.createQuery("from UserBot", UserBot.class).getResultList();
        }
    }

    @Override
    public UserBot getUserBot(Long id) {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            return session.get(UserBot.class,id);
        }
    }

    @Override
    public UserBot getUserBot(User user) {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            return session.byNaturalId(UserBot.class).using("tgId",user.getId()).load();
            //return session.createQuery("from UserBot where tgId="+ user.getId(), UserBot.class).getSingleResult();
        }
    }

    @Override
    public UserBot update(UserBot userBot) {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.update(userBot);
            session.getTransaction().commit();
            return userBot;
        }
    }
    @Override
    public UserBot save(UserBot userBot) {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.save(userBot);
            session.getTransaction().commit();
            return userBot;
        }
    }

    @Override
    public UserBot deleteUserBot(UserBot userBot) {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.delete(userBot);
            session.getTransaction().commit();
        }
        return userBot;
    }
}
