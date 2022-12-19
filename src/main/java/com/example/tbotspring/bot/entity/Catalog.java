package com.example.tbotspring.bot.entity;

import javax.persistence.*;

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

    public Catalog() {
    }

    public Catalog(String title, Long shopId, Long fatherid, Long level) {
        this.fatherId = fatherid;
        this.title = title;
        this.shopId = shopId;
        this.level = level;
    }

    @Override
    public String toString() {
        return "Catalog{" +
                "id=" + id +
                ", fatherId=" + fatherId +
                ", title='" + title + '\'' +
                ", shopId=" + shopId +
                ", level=" + level +
                '}';
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
}
