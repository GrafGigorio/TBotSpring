package ru.masich.bot.action.store;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.StartBot;
import ru.masich.bot.DAO.IMPL.AwaitDAOimpl;
import ru.masich.bot.DAO.IMPL.LastMessageDAOimpl;
import ru.masich.bot.DAO.IMPL.StoreDAOimpl;
import ru.masich.bot.DAO.IMPL.UserBotDAOImpl;
import ru.masich.bot.DAO.interfaces.AwaitDao;
import ru.masich.bot.DAO.interfaces.LastMessageDAO;
import ru.masich.bot.DAO.interfaces.StoreDao;
import ru.masich.bot.DAO.interfaces.UserBotDAO;
import ru.masich.bot.Var;
import ru.masich.bot.action.MessageBot;
import ru.masich.bot.entity.Await;
import ru.masich.bot.entity.LastMessage;
import ru.masich.bot.entity.Store;
import ru.masich.bot.entity.UserBot;
import ru.masich.bot.menu.CatalogMenu;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StoreAction {
    private final UserBot userBot;
    private final Update update;
    private final StoreDao storeDao = new StoreDAOimpl();
    private final UserBotDAO userBotDAO = new UserBotDAOImpl();
    private final AwaitDao awaitDao = new AwaitDAOimpl();
    private final LastMessageDAO lastMessageDAO = new LastMessageDAOimpl();
    private StartBot startBot = null;
    static Logger logger = LogManager.getLogger(StoreAction.class);
    public StoreAction(UserBot userBot, Update update, StartBot startBot) {
        this.userBot = userBot;
        this.update = update;
        this.startBot = startBot;
    }

    public void create()
    {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+") create");
        //Роль в магазин
        Map<String, Object> roleShop = new LinkedHashMap<>();
        roleShop.put(String.valueOf(userBot.getId()),0);
        Store store = new Store(userBot.getId(),update.getMessage().getText(),roleShop);
        storeDao.saveOrUpdateStore(store);
        //Роль в пользователя
        Map<String, Object> roleUser = userBot.getRole();
        roleUser.put(String.valueOf(store.getId()), 0);
        userBotDAO.update(userBot);

        List<Await> awaits = awaitDao.getAll(userBot.getId());
        awaitDao.delete(awaits.get(0));

        MessageBot messageBot = new MessageBot(startBot,update,userBot);

        messageBot.sendMessage("Магазин " + update.getMessage().getText() + " успешно созданн!");
        Message message = messageBot.sendMenu(CatalogMenu.getStartMenu(userBot));
        //Ползунок последнего сообщения
        LastMessage lastMessage = lastMessageDAO.getLastMessage(userBot.getId());
        lastMessage.setLastMessageId(message.getMessageId().longValue());
        lastMessageDAO.updateLastMessage(lastMessage);
    }
    public void edit(Long objId)
    {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+") edit " +objId);
        List<Await> awaits = awaitDao.getAll(userBot.getId());
        awaitDao.delete(awaits.get(0));


        Store store = storeDao.getStore(objId);
        store.setTitle(update.getMessage().getText());
        storeDao.saveOrUpdateStore(store);

        MessageBot messageBot = new MessageBot(startBot,update,userBot);

        messageBot.sendMessage("Магазин #"+ store.getId() + "  переименован на "+store.getTitle());
        Message message = sendMenu(userBot.getTgId(), Var.getStoresTitle, CatalogMenu.getStoresList(userBot));
        //Ползунок последнего сообщения
        LastMessage lastMessage = lastMessageDAO.getLastMessage(userBot.getId());
        lastMessage.setLastMessageId(message.getMessageId().longValue());
        lastMessageDAO.updateLastMessage(lastMessage);
    }
    public Message sendMenu(Long who, String txt, InlineKeyboardMarkup kb) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")<< sendMenu " + txt);
        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("HTML").text(txt)
                .replyMarkup(kb).build();
        try {
            return startBot.execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
