package ru.masich.bot.DAO;

import ru.masich.bot.entity.LastMessage;

public interface LastMessageDAO {
    public LastMessage getLastMessage(Integer userId);
    public LastMessage getLastMessage(Long userId);
    public void setLastMessage(LastMessage lastMessage);
    public void updateLastMessage(LastMessage lastMessage);
    public void deleteLastMessage(LastMessage id);
}
