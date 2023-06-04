package ru.masich.bot.DAO.interfaces;

import ru.masich.bot.entity.BIgObject;

public interface BigObjectDAO {
    BIgObject get(int id);
    void save(int userId, String data);
    void save(BIgObject object);
}
