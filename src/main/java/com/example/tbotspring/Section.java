package com.example.tbotspring;

import javax.persistence.*;

@Entity
@Table(name = "section")
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private Long fatherid;
    private String title;
    private Long shopId;
    private Long level;

    public Section() {
    }

    public Section(Long fatherid, String title, Long shopId, Long level) {
        this.fatherid = fatherid;
        this.title = title;
        this.shopId = shopId;
        this.level = level;
    }

    @Override
    public String toString() {
        return "section{" +
                "id=" + id +
                ", fatherid=" + fatherid +
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

    public Long getFatherid() {
        return fatherid;
    }

    public void setFatherid(Long fatherid) {
        this.fatherid = fatherid;
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
