package ru.masich.bot.entity;

import javax.persistence.*;
import java.util.*;

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

    public ProductSize getProductSize(int psID) {
        Map<String, Map<String, Object>> ps = (Map<String, Map<String, Object>>) productAttributes.get("check_box_prop");
        Map<String, Object> retp = ps.get(String.valueOf(psID));
        if (retp == null)
            return null;
        return new ProductSize(id,
                psID,
                retp.get("act").toString(),
                retp.get("sel") != null,
                retp.get("tit").toString());
    }

    public void setProductSize(List<ProductSize> size) {
        Map<String, Map<String, Object>> psd = new IdentityHashMap<>();
        for (ProductSize ps : size) {
            if (ps.getProductId() != id) {
                continue;
            }
            Map<String, Object> vals = new IdentityHashMap<>();
            vals.put("act", ps.getAction());
            if (ps.isDefaultSize())
                vals.put("sel", ps.isDefaultSize());
            vals.put("tit", ps.getTitle());
            psd.put(String.valueOf(ps.getNumber()), vals);
        }
        productAttributes.put("check_box_prop", psd);
    }

    public ProductCount getProductCount(int psID) {
        Map<String, Map<String, Object>> ps = (Map<String, Map<String, Object>>) productAttributes.get("count_property");
        Map<String, Object> retp = ps.get(String.valueOf(psID));
        if (retp == null)
            return null;
        return new ProductCount(
                id,
                psID,
                Integer.parseInt(retp.get("cou").toString()),
                retp.get("act").toString(),
                retp.get("tit").toString()
        );
    }

    public void setProductCount(List<ProductCount> count) {
        Map<String, Map<String, Object>> cp = new IdentityHashMap<>();
        for (ProductCount cou : count) {
            if (cou.getProductId() != id) {
                continue;
            }
            Map<String, Object> vals = new IdentityHashMap<>();

            vals.put("act", cou.getAction());
            vals.put("tit", cou.getTitle());
            vals.put("cou", cou.getCount());

            cp.put(String.valueOf(cou.getNumber()), vals);
        }
        productAttributes.put("count_property", cp);
    }

    public StringBuilder check(Product o) {
        StringBuilder upd = new StringBuilder();

        if (shopId != o.getShopId()) {
            upd.append("Товар id: ");
            upd.append(id);
            upd.append(" будет изменен id магазина\r\nс");
            upd.append(shopId);
            upd.append("\r\n на ");
            upd.append(o.shopId);
            upd.append("\r\n");
        }
        if (catalogId != o.getCatalogId()) {
            upd.append("Товар id: ");
            upd.append(id);
            upd.append(" будет изменен id каталога \r\n c ");
            upd.append(catalogId);
            upd.append("\r\nна ");
            upd.append(o.catalogId);
            upd.append("\r\n");
        }


        Map<String,Object> localParams = productAttributes;
        Map<String,Object> remoteParams = o.getProductAttributes();

        if (!localParams.get("title").equals(remoteParams.get("title"))) {
            upd.append("Товар id: ");
            upd.append(id);
            upd.append(" будет изменен заголовок товара \r\n c ");
            upd.append(localParams.get("title"));
            upd.append("\r\nна ");
            upd.append(remoteParams.get("title"));
            upd.append("\r\n");
        }
        if (!localParams.get("main_photo").equals(remoteParams.get("main_photo"))) {
            upd.append("Товар id: ");
            upd.append(id);
            upd.append(" будет изменена картинка товара \r\n c ");
            upd.append(localParams.get("main_photo"));
            upd.append("\r\nна ");
            upd.append(remoteParams.get("main_photo"));
            upd.append("\r\n");
        }

        String localMes = localParams.get("measurement").toString();
        String remoteMes = remoteParams.get("measurement").toString();

        if(!localMes.equals(remoteMes))
        {
            upd.append("Товар id: ");
            upd.append(id);
            upd.append(" будет именено свойство еденицы измерения \r\n");
        }

        Map<String,Map<String,String>> localSize = (Map<String, Map<String, String>>) localParams.get("check_box_prop");
        Map<String,Map<String,String>> remoteSize = (Map<String, Map<String, String>>) remoteParams.get("check_box_prop");

        if(localSize.size() > remoteSize.size()) {
            upd.append("Товар id: ");
            upd.append(id);
            upd.append(" будет удаленно свойство/а размера \r\n");
        }
        if(localSize.size() < remoteSize.size()) {
            upd.append("Товар id: ");
            upd.append(id);
            upd.append(" будет созданно свойство/а размера \r\n");
        }
        Map<String,Map<String,String>> localCount = (Map<String, Map<String, String>>) localParams.get("count_property");
        Map<String,Map<String,String>> remoteCount = (Map<String, Map<String, String>>) remoteParams.get("count_property");

        if(localCount.size() > remoteCount.size()) {
            upd.append("Товар id: ");
            upd.append(id);
            upd.append(" будет удаленно свойство/а количества \r\n");
        }
        if(localCount.size() < remoteCount.size()) {
            upd.append("Товар id: ");
            upd.append(id);
            upd.append(" будет созданно свойство/а количества \r\n");
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
