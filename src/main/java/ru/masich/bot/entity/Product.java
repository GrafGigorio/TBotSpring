package ru.masich.bot.entity;

import javax.persistence.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
                ", productAttributes=" + productAttributes +
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

    public ProductSize getPtoductSize(int psID)
    {
        List<Map<String, Object>> ps = (List<Map<String, Object>>) productAttributes.get("check_box_prop");
        Map<String, Object> retp = ps.stream().filter(x -> {
            return (int)x.get("id") == psID;
        }).findFirst().get();
        return new ProductSize(id,(int)retp.get("id"),retp.get("act").toString(),retp.get("sel")==null?false:true,retp.get("tit").toString());
    }
    public StringBuilder check(Product o) {
        StringBuilder upd = new StringBuilder();
        if (this == o) return new StringBuilder();

        Product product = (Product) o;
        if (shopId != product.shopId) {
            upd.append("Товар id: ");
            upd.append(id);
            upd.append(" будет изменен id магазина\r\nс");
            upd.append(shopId);
            upd.append("\r\n на ");
            upd.append(product.shopId);
        }
        if (catalogId == product.catalogId) {
            upd.append("Товар id: ");
            upd.append(id);
            upd.append(" будет изменен id каталога \r\n c ");
            upd.append(catalogId);
            upd.append("\r\nна ");
            upd.append(product.catalogId);
        }
        if(!Objects.equals(productAttributes, product.productAttributes))
        {
            upd.append("Товар id: ");
            upd.append(id);
            upd.append(" будут изменены параметры.\r\n");
            for(Map.Entry<String, Object> ds : productAttributes.entrySet())
            {
                if(!ds.getValue().equals(product.getProductAttributes().get(ds.getKey())))
                {
                    upd.append(" - \t параметр: ");
                    upd.append(ds.getKey());
                    upd.append(" --\t с ");
                    upd.append(ds.getValue());
                    upd.append("\r\n");
                    upd.append(" --\t на ");
                    upd.append(product.getProductAttributes().get(ds.getKey()));
                    upd.append("\r\n");
                }
            }
        }
        return upd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id && shopId == product.shopId && catalogId == product.catalogId && Objects.equals(productAttributes, product.productAttributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, shopId, catalogId, productAttributes);
    }
}
