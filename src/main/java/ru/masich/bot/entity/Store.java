package ru.masich.bot.entity;


import javax.persistence.*;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "shop")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private Long userid;
    private String title;
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> role;


    public Store(Long userid, String title, Map<String, Object> role) {
        this.userid = userid;
        this.title = title;
        this.role = role;
    }

    public Store() {

    }

    @Override
    public String toString() {
        return "Store{" +
                "id=" + id +
                ", userid=" + userid +
                ", title='" + title + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Object> getRole() {
        return role;
    }

    public void setRole(Map<String, Object> role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return id.equals(store.id) && userid.equals(store.userid) && Objects.equals(title, store.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userid, title);
    }
}
