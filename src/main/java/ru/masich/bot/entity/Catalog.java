package ru.masich.bot.entity;

import javax.persistence.*;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "catalog")
public class Catalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private Long fatherId;
    private String title;
    private Long shopId;
    private Long level;
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> catalog_atributes;

    public Catalog() {
    }

    public Catalog(String title, Long shopId, Long fatherid, Long level, Map<String, Object> catalog_atributes) {
        this.fatherId = fatherid;
        this.title = title;
        this.shopId = shopId;
        this.level = level;
        this.catalog_atributes = catalog_atributes;
    }

    @Override
    public String toString() {
        return "Catalog{" +
                "id=" + id +
                ", fatherId=" + fatherId +
                ", title='" + title + '\'' +
                ", shopId=" + shopId +
                ", level=" + level +
                ", product_attributes=" + catalog_atributes +
                '}';
    }

    public Map<String, Object> getCatalog_atributes() {
        return catalog_atributes;
    }

    public void setCatalog_atributes(Map<String, Object> catalog_atributes) {
        this.catalog_atributes = catalog_atributes;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFatherId() {
        return fatherId;
    }

    public void setFatherId(Long fatherid) {
        this.fatherId = fatherid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public StringBuilder check(Catalog o) {
        StringBuilder upd = new StringBuilder();
        if (this == o) return new StringBuilder();
        Catalog catalog = (Catalog) o;

        if (catalog.fatherId != fatherId) {
            upd.append("Каталог id: ");
            upd.append(id);
            upd.append(" будет изменен родитель \r\n\tс ");
            upd.append(fatherId);
            upd.append("\r\n\tна ");
            upd.append(catalog.getFatherId());
        }
        if (!title.equals(catalog.getTitle())) {
            upd.append("Каталог id: ");
            upd.append(id);
            upd.append(" будет изменен заголовок \r\n\tс ");
            upd.append(title);
            upd.append("\r\n\tна ");
            upd.append(catalog.getTitle());
        }
        if (!catalog.getShopId().equals(shopId)) {
            upd.append("Каталог id: ");
            upd.append(id);
            upd.append(" будет изменен id магазина \r\n\tс ");
            upd.append(shopId);
            upd.append("\r\n\tна ");
            upd.append(catalog.getShopId());
        }
        if (!catalog.getLevel().equals(level)) {
            upd.append("Каталог id: ");
            upd.append(id);
            upd.append(" будет изменен уровень вроженности \r\n\tс ");
            upd.append(level);
            upd.append(" \r\n\tна ");
            upd.append(catalog.getLevel());
        }
        if (!catalog.getCatalog_atributes().get("photo").equals(catalog_atributes.get("photo"))) {
            upd.append("Каталог id: ");
            upd.append(id);
            upd.append(" будет изменено фото \r\n\tс ");
            upd.append(catalog_atributes.get("photo"));
            upd.append("\r\n\tна ");
            upd.append(catalog.getCatalog_atributes().get("photo"));
        }
        return upd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Catalog catalog = (Catalog) o;
        return Objects.equals(id, catalog.id) && Objects.equals(fatherId, catalog.fatherId) && Objects.equals(title, catalog.title) && Objects.equals(shopId, catalog.shopId) && Objects.equals(level, catalog.level) && Objects.equals(catalog_atributes, catalog.catalog_atributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fatherId, title, shopId, level, catalog_atributes);
    }
}
