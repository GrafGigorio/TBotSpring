package ru.masich.bot.entity;


import javax.persistence.*;
import java.util.Map;

@Entity
@Table(name = "object_send")
public class ObjectSend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private Long userId;
    private int objectId;
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> property;

    public ObjectSend(Long userId, int objectId, Map<String, Object> propertys) {
        this.userId = userId;
        this.objectId = objectId;
        this.property = propertys;
    }

    public ObjectSend() {
    }

    public ObjectSend(Long chatId) {
        userId = chatId;
    }

    @Override
    public String toString() {
        return "ObjectSend{" +
                "id=" + id +
                ", userId=" + userId +
                ", objectId=" + objectId +
                '}';
    }

    public Map<String, Object> getProperty() {
        return property;
    }

    public void setProperty(Map<String, Object> propertys) {
        this.property = propertys;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }
}
