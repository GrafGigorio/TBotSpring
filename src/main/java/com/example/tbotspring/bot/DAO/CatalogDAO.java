package com.example.tbotspring.bot.DAO;

import com.example.tbotspring.bot.entity.Catalog;

import java.util.List;

public interface CatalogDAO {
    List<Catalog> getCatalogAllStore(Long shopId);
    List<Catalog> getChildren(Long catalogID);
    void set(Catalog catalog);
    void delete(Catalog catalog);
    void update(Catalog catalog);
}
