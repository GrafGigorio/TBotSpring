package com.example.tbotspring.bot.entity;

import javax.persistence.*;

@Entity
@Table(name = "Await")
public class Await {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private Long userid;
    private String command;

    public Await() {
    }

    public Await(String command) {
        this.command = command;
    }

    public Await(Long tgUserId, String command) {
        this.userid = tgUserId;
        this.command = command;
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

    public void setUserid(Long tgUserId) {
        this.userid = tgUserId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return "Await{" +
                "id=" + id +
                ", tgUserId=" + userid +
                ", command='" + command + '\'' +
                '}';
    }

}
