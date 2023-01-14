package ru.masich.bot.DAO;

import ru.masich.bot.entity.Catalog;

import java.util.List;

public interface CatalogDAO {
    List<Catalog> getCatalogAllStore(Long shopId);
    List<Catalog> getChildren(Long catalogID);
    Catalog get(Long catalogID);
    void set(Catalog catalog);
    void delete(Catalog catalog);
    void update(Catalog catalog);
}
