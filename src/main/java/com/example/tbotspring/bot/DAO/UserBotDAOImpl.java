package com.example.tbotspring.bot.DAO;

import com.example.tbotspring.bot.Var;
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
    SessionFactory sessionFactory;
    Session session;

    public UserBotDAOImpl() {
        this.sessionFactory = Var.sessionFactory;
        this.session = sessionFactory.openSession();
    }

    @Override
    public List<UserBot> getAllUserBot() {
        try {
            session.beginTransaction();
            return session.createQuery("from UserBot", UserBot.class).getResultList();
        }
        finally {
            session.close();
        }
    }

    @Override
    public UserBot getUserBot(Long id) {
        try {
            session.beginTransaction();
            return session.get(UserBot.class,id);
        }
        finally {
            session.close();
        }
    }

    @Override
    public UserBot getUserBot(User user) {
        try {
            session.beginTransaction();
            return session.byNaturalId(UserBot.class).using("tgId",user.getId()).load();
            //return session.createQuery("from UserBot where tgId="+ user.getId(), UserBot.class).getSingleResult();
        }
        finally {
            session.close();
        }
    }

    @Override
    public UserBot update(UserBot userBot) {
        try {
            session.beginTransaction();
            session.update(userBot);
            session.getTransaction().commit();
            return userBot;
        }
        finally {
            session.close();
        }
    }
    @Override
    public UserBot save(UserBot userBot) {
        try {
            session.beginTransaction();
            session.save(userBot);
            session.getTransaction().commit();
            return userBot;
        }
        finally {
            session.close();
        }
    }

    @Override
    public UserBot deleteUserBot(UserBot userBot) {
        try {
            session.beginTransaction();
            session.delete(userBot);
            session.getTransaction().commit();
        }
        finally {
            session.close();
        }
        return userBot;
    }
}
