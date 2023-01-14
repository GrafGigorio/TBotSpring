package ru.masich.bot.entity;

import javax.persistence.*;
import java.util.Map;

@Entity
@Table(name = "Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    private int shopId;
    private int catalogId;
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> productAttributes;

    public Product() {
    }

    public Product(int shopId, int catalogId, Map<String, Object> productAttributes) {
        this.shopId = shopId;
        this.catalogId = catalogId;
        this.productAttributes = productAttributes;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", shopId=" + shopId +
                ", catalogId=" + catalogId +
                ", customerAttributes=" + productAttributes +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(int catalogId) {
        this.catalogId = catalogId;
    }

    public Map<String, Object> getProductAttributes() {
        return productAttributes;
    }

    public void setProductAttributes(Map<String, Object> customerAttributes) {
        this.productAttributes = customerAttributes;
    }
}
