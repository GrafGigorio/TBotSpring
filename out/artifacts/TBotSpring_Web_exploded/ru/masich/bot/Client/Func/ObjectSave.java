package ru.masich.bot.Client.Func;

import ru.masich.bot.DAO.IMPL.ObjectSendDAOimpl;
import ru.masich.bot.DAO.interfaces.ObjectSendDAO;
import ru.masich.bot.entity.ObjectSend;

public class ObjectSave {

    public static void save(Long id, Long tgUser, int objectId)
    {
        ObjectSendDAO objectSendDAO = new ObjectSendDAOimpl();
        ObjectSend objectSend = new ObjectSend(tgUser);
        objectSend.setObjectId(objectId);
        objectSend.setId(id);

        objectSendDAO.updateObject(objectSend);
    }

    public static Long prepare(Long tgUser)
    {
        ObjectSendDAO objectSendDAO = new ObjectSendDAOimpl();
        return objectSendDAO.updateObject(new ObjectSend(tgUser)).getId();
    }
}
