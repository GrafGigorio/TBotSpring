package ru.masich.bot.DAO.interfaces;

import ru.masich.bot.entity.Catalog;

import java.util.List;

public interface CatalogDAO {
    List<Catalog> getCatalogAllStore(Long shopId);
    List<Catalog> getChildren(Long catalogID);
    Catalog get(Long catalogID);
    Catalog get(int catalogID);
    Catalog get(String catalogID);
    void set(Catalog catalog);
    void delete(Catalog catalog);
    void update(Catalog catalog);
}
