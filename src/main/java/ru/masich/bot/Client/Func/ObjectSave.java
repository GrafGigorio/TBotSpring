package ru.masich.bot.Client.Func;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.masich.bot.DAO.IMPL.ObjectSendDAOimpl;
import ru.masich.bot.DAO.interfaces.ObjectSendDAO;
import ru.masich.bot.entity.ObjectSend;

public class ObjectSave {
    static Logger logger = LogManager.getLogger(ObjectSave.class);

    public static void save(Long id, Long tgUser, int objectId) {
        logger.info("<<  save id " + id + " tgUser " + tgUser + " objectId " + objectId);
        ObjectSendDAO objectSendDAO = new ObjectSendDAOimpl();
        ObjectSend objectSend = new ObjectSend(tgUser);
        objectSend.setObjectId(objectId);
        objectSend.setId(id);

        objectSendDAO.updateObject(objectSend);
    }

    public static Long prepare(Long tgUser) {
        logger.info("<<  prepare tgUser " + tgUser);
        ObjectSendDAO objectSendDAO = new ObjectSendDAOimpl();
        return objectSendDAO.updateObject(new ObjectSend(tgUser)).getId();
    }
}
