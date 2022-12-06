package com.example.tbotspring.bot.DAO;

import com.example.tbotspring.bot.entity.LastMessage;
import com.example.tbotspring.bot.entity.UserBot;

public interface LastMessageDAO {
    public LastMessage getLastMessage(Long userId);
    public void setLastMessage(LastMessage lastMessage);
    public void updateLastMessage(LastMessage lastMessage);
    public void deleteLastMessage(LastMessage id);
}
