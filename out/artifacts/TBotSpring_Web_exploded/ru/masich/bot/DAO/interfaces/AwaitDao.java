package ru.masich.bot.DAO.interfaces;

import ru.masich.bot.entity.Await;

import java.util.List;

public interface AwaitDao {
    Await saveOrUpdate(Await await);
    Await set(Await await);
    Await get(Long userid);
    List<Await> getAll(Long userid);
    Await delete(Await await);
}
