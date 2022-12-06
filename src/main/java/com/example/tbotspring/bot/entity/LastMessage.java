package com.example.tbotspring.bot.entity;

import javax.persistence.*;

@Entity
@Table(name = "lastMessage")
public class LastMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne
    @JoinColumn(name = "userid")
    private UserBot userBot;
    private Long lastMessageId;

    public LastMessage() {
    }

    public LastMessage(UserBot userBot, Long lastMessageId) {
        this.userBot = userBot;
        this.lastMessageId = lastMessageId;
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

    @Override
    public String toString() {
        return "lastMessage{" +
                "id=" + id +
                ", userId=" + userBot +
                ", lastMessageId=" + lastMessageId +
                '}';
    }
}
