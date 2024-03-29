package ru.masich.bot.action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.StartBot;
import ru.masich.bot.DAO.IMPL.LastMessageDAOimpl;
import ru.masich.bot.DAO.IMPL.UserBotDAOImpl;
import ru.masich.bot.DAO.interfaces.LastMessageDAO;
import ru.masich.bot.DAO.interfaces.UserBotDAO;
import ru.masich.bot.entity.LastMessage;
import ru.masich.bot.entity.UserBot;
import ru.masich.bot.menu.CatalogMenu;

public class MessageBot {
    private StartBot startBot;
    private final LastMessageDAO lastMessageDAO = new LastMessageDAOimpl();
    private final UserBotDAO userBotDAO = new UserBotDAOImpl();
    private Update update;
    private UserBot userBot;
    static Logger logger = LogManager.getLogger(MessageBot.class);
    public MessageBot(StartBot startBot, Update update,UserBot userBot) {
        this.startBot = startBot;
        this.update = update;
        this.userBot = userBot;
    }

    public void execute()
    {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< execute " + update.getMessage().getText());
        //Проверяем пользователя на наличие его в наших списках
        //getFrom(update);


        this.sendMessage("Команда: " + update.getMessage().getText() + " не распознанна!");
        Message message = this.sendMenu(CatalogMenu.getStartMenu(userBot));

        LastMessage lastMessage = lastMessageDAO.getLastMessage(userBotDAO.getUserBot(update.getMessage().getFrom()).getId());

        if(lastMessage == null)
        {
            lastMessage = new LastMessage(userBot,message.getMessageId());
            lastMessageDAO.setLastMessage(lastMessage);
            return;
        }

        lastMessage.setLastMessageId(message.getMessageId());
        lastMessageDAO.updateLastMessage(lastMessage);

    }
    public Message sendMessage(String mes)
    {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< sendMessage mes " + mes);
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
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< sendMenu menu " + menu.getText());
        try {
            return startBot.execute(menu);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
