package ru.masich.bot.DAO.IMPL;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.masich.bot.DAO.interfaces.ChartDAO;
import ru.masich.bot.Var;
import ru.masich.bot.entity.Chart;

import java.util.List;

public class ChartDAOimpl implements ChartDAO {
    SessionFactory sessionFactory;
    Session session;
    static Logger logger = LoggerFactory.getLogger(ChartDAOimpl.class);
    public ChartDAOimpl() {
        this.sessionFactory = Var.sessionFactory;
        this.session = sessionFactory.getCurrentSession();
    }

    @Override
    public Chart getActiveChart(Long userId) {
        logger.info("<< getActiveChart userId " + userId);
        try{
            session = sessionFactory.openSession();
            session.beginTransaction();
            List<Chart> ret = session
                    .createQuery("from Chart where user_id="+userId + " and chart_active=true", Chart.class)
                    .getResultList();
            if(ret.size() > 0)
                return ret.get(0);
            else
                return null;
        }
        finally {
            session.close();
        }
    }

    @Override
    public Chart get(int chartId) {
        logger.info("<< get chartId "+ chartId);
        try{
            session = sessionFactory.openSession();
            session.beginTransaction();
            return session.get(Chart.class, chartId);
        }
        finally {
            session.close();
        }
    }

    @Override
    public List<Chart> getClosedChartList(int userId) {
        logger.info("<< getClosedChartList userId "+userId);
        try{
            session = sessionFactory.openSession();
            session.beginTransaction();
            List<Chart> ret = session
                    .createQuery("from Chart where user_id="+userId + " and chart_active=false", Chart.class)
                    .getResultList();
            if(ret.size() > 0)
                return ret;
            else
                return null;
        }
        finally {
            session.close();
        }
    }

    @Override
    public Chart updateOrAdd(Chart chart) {
        logger.info("<< updateOrAdd chart "+chart);
        try{
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.saveOrUpdate(chart);
            session.getTransaction().commit();
            return chart;
        }
        finally {
            session.close();
        }
    }
}
