package com.example.tbotspring;

import javax.persistence.*;

@Entity
@Table(name = "shop")
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private Long mastertgid;
    private String title;

    public Shop() {
    }

    public Shop(Long mastertgid, String title) {
        this.mastertgid = mastertgid;
        this.title = title;
    }

    @Override
    public String toString() {
        return "Shop{" +
                "id=" + id +
                ", mastertgid=" + mastertgid +
                ", title='" + title + '\'' +
                '}';
    }

    public Long getMastertgid() {
        return mastertgid;
    }

    public void setMastertgid(Long mastertgid) {
        this.mastertgid = mastertgid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
