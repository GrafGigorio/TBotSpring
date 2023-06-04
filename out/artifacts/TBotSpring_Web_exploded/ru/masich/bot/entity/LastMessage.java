package ru.masich.bot.entity;

import javax.persistence.*;

@Entity
@Table(name = "lastMessage")
public class LastMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne
    @JoinColumn(name = "userId")
    private UserBot userBot;
    private Long lastMessageId;

    public LastMessage() {
    }

    public LastMessage(UserBot userBot, Long lastMessageId) {
        this.userBot = userBot;
        this.lastMessageId = lastMessageId;
    }

    public LastMessage(UserBot userBot, Integer lastMessageId) {
        this.userBot = userBot;
        this.lastMessageId = Long.valueOf(lastMessageId);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserBot getUserId() {
        return userBot;
    }

    public void setUserId(UserBot userBot) {
        this.userBot = userBot;
    }

    public Long getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(Long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }
    public void setLastMessageId(Integer lastMessageId) {
        this.lastMessageId = Long.valueOf(lastMessageId);
    }

    @Override
    public String toString() {
        return "lastMessage{" +
                "id=" + id +
                ", userId=" + userBot +
                ", lastMessageId=" + lastMessageId +
                '}';
    }
}
