package ru.masich.bot.DAO.interfaces;

import ru.masich.bot.entity.Store;

import java.util.List;

public interface StoreDao {
    List<Store> getAllStore();
    List<Store> getAllManagementStore();
    List<Store> getAllUserStores(Long userId);
    Store getStore(Long storeId);
    void saveOrUpdateStore(Store store);
    void deleteStore(Store store);
}
