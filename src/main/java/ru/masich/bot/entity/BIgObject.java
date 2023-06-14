package ru.masich.bot.entity;

import javax.persistence.*;
import java.util.Map;

@Entity
@Table(name = "bigObj")
public class BIgObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    private int userId;
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> data;


    public BIgObject() {
    }

    public BIgObject(int userId, Map<String, Object> data) {
        this.userId = userId;
        this.data = data;
    }
    public BIgObject(int userId, String data) {
        this.userId = userId;
        HashMapConverter converter = new HashMapConverter();
        this.data = converter.convertToEntityAttribute(data);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BIgObject{" +
                "id=" + id +
                ", userId=" + userId +
                ", data=" + data +
                '}';
    }
}
