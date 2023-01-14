package ru.masich.bot.DAO;

import ru.masich.bot.entity.Product;

import java.util.List;

public interface ProductDAO {
    Product get(int productId);
    List<Product> getStore(Long shopID);
    List<Product> getCatalog(Long catalogID);
    void set(Product product);
    void delete(int productId);
}
