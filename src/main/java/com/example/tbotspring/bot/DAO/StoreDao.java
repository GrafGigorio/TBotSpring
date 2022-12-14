package com.example.tbotspring.bot.DAO;

import com.example.tbotspring.bot.entity.Store;

import java.util.List;

public interface StoreDao {
    List<Store> getAllStore();
    List<Store> getAllUserStores(Long userId);
    Store getStore(Long storeId);
    Store saveOrUpdateStore(Store store);
    Store deleteStore(Store store);
}
