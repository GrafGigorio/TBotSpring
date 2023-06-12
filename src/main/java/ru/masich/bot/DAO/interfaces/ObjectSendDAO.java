package ru.masich.bot.DAO.interfaces;

import ru.masich.bot.entity.ObjectSend;

public interface ObjectSendDAO {
    ObjectSend get(Long id);
    ObjectSend getObject(Long id);
    ObjectSend updateObject(ObjectSend object);
    ObjectSend deleteObject(Long objectId);

}
