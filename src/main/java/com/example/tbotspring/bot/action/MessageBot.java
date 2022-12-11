package com.example.tbotspring.bot.action;

import com.example.tbotspring.StartBot;
import com.example.tbotspring.bot.DAO.LastMessageDAO;
import com.example.tbotspring.bot.DAO.LastMessageDAOimpl;
import com.example.tbotspring.bot.DAO.UserBotDAO;
import com.example.tbotspring.bot.DAO.UserBotDAOImpl;
import com.example.tbotspring.bot.entity.LastMessage;
import com.example.tbotspring.bot.entity.UserBot;
import com.example.tbotspring.bot.menu.Menu;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MessageBot {
    private StartBot startBot;
    private LastMessageDAO lastMessageDAO = new LastMessageDAOimpl();
    private UserBotDAO userBotDAO = new UserBotDAOImpl();
    private  Update update;
    private UserBot userBot;
    public MessageBot(StartBot startBot, Update update,UserBot userBot) {
        this.startBot = startBot;
        this.update = update;
        this.userBot = userBot;
    }

    public void execute(Update update)
    {
        this.sendMessage("Команда: " + update.getMessage().getText() + " не распознанна!");
        Message message = this.sendMenu(Menu.getStartMenu(userBot));

        LastMessage lastMessage = lastMessageDAO.getLastMessage(userBotDAO.getUserBot(update.getMessage().getFrom()).getId());
        if(lastMessage != null)
        {
            lastMessage.setLastMessageId(Long.valueOf(message.getMessageId()));
            lastMessageDAO.updateLastMessage(lastMessage);
            return;
        }

        lastMessageDAO.setLastMessage(new LastMessage(userBot,message.getMessageId()));
    }
    public Message sendMessage(String mes)
    {
        SendMessage smd = SendMessage.builder().chatId(userBot.getTgId())
                .text(mes).build();
        try {
            return startBot.execute(smd);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    public Message sendMenu(SendMessage menu)
    {
        try {
            return startBot.execute(menu);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
