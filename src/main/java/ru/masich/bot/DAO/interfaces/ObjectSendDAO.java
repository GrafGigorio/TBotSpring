package ru.masich.bot.DAO.interfaces;

import ru.masich.bot.entity.ObjectSend;

public interface ObjectSendDAO {
    ObjectSend getObject(Long objectId);
    ObjectSend updateObject(ObjectSend object);
    ObjectSend deleteObject(Long objectId);
}
