package ru.masich.bot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.StartBot;
import ru.masich.bot.DAO.IMPL.*;
import ru.masich.bot.DAO.interfaces.*;
import ru.masich.bot.action.Button;
import ru.masich.bot.action.MessageBot;
import ru.masich.bot.entity.*;
import ru.masich.bot.menu.CatalogMenu;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Proxy {

    private final StoreDao storeDao = new StoreDAOimpl();
    private final AwaitDao awaitDao = new AwaitDAOimpl();
    private final UserBotDAO userBotDAO = new UserBotDAOImpl();
    private final LastMessageDAO lastMessageDAO = new LastMessageDAOimpl();
    private final CatalogDAO catalogDAO = new CatalogDAOimpl();
    private StartBot startBot;
    private Update update;
    private UserBot userBot;

    public Proxy(StartBot startBot) {
        this.startBot = startBot;
    }
    static Logger logger = LogManager.getLogger(Proxy.class);

    public void proxy(Update update)
    {
        String logd= "<< proxy " + (update.getCallbackQuery() != null ? update.getCallbackQuery() : update.getMessage());
        if(logd.length() > 100)
            logd = logd.substring(0,100) + " ...";
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+logd);
        this.update = update;
        getFrom(update);//Сохраняем поле пользователя userBot и проверяем наличие обновлений полей

        //Проверряем наличие отложенных заданий(В случаях где от пользователя ожидается получение названия какого либо обьекта)

        List<Await> awaits = awaitDao.getAll(userBot.getId());
        MessageBot messageBot = new MessageBot(startBot,update,userBot);
        Button button = new Button(startBot,update);

        if(awaits.size() > 0)
        {
            Await await = awaits.get(0);
            if(update.getMessage() == null)
            {
                messageBot.sendMessage("Отложенная команда: " + await.getCommand() + " не распознанна!");
                Message message = messageBot.sendMenu(CatalogMenu.getStartMenu(userBot));
                LastMessage lastMessage = lastMessageDAO.getLastMessage(userBot.getId());
                lastMessage.setLastMessageId(message.getMessageId());
                lastMessageDAO.updateLastMessage(lastMessage);
                awaitDao.delete(await);
                return;
            }

            Long objId = -1L;
            long objId2 = -1L;
            Long objId3 = -1L;
            String mes = "";
            String[] comandSeq = await.getCommand().split(":");
            if(comandSeq.length > 2)
            {
                if(comandSeq.length > 3)
                    objId2 = Long.parseLong(comandSeq[3]);
                if(comandSeq.length > 4)
                    objId3 = Long.valueOf(comandSeq[4]);
                objId = Long.valueOf(comandSeq[2]);
                mes = comandSeq[0]+":"+comandSeq[1]+":";
            }
            else
            {
                mes = update.getMessage().getText();
            }

            if(await.getCommand().equals(Var.createStore))
                mes = Var.createStore;
            //Перечисление отложенных комманд
            switch (mes)
            {
                //Создание магазина
                case Var.createStore -> {

                    //Роль в магазин
                    Map<String, Object> roleShop = new LinkedHashMap<>();
                    roleShop.put(String.valueOf(userBot.getId()),0);
                    Store store = new Store(userBot.getId(),update.getMessage().getText(),roleShop);
                    storeDao.saveOrUpdateStore(store);
                    //Роль в пользователя
                    Map<String, Object> roleUser = userBot.getRole();
                    roleUser.put(String.valueOf(store.getId()), 0);
                    userBotDAO.update(userBot);


                    awaitDao.delete(await);
                    messageBot.sendMessage("Магазин " + update.getMessage().getText() + " успешно созданн!");
                    Message message = messageBot.sendMenu(CatalogMenu.getStartMenu(userBot));
                    //Ползунок последнего сообщения
                    LastMessage lastMessage = lastMessageDAO.getLastMessage(userBot.getId());
                    lastMessage.setLastMessageId(message.getMessageId().longValue());
                    lastMessageDAO.updateLastMessage(lastMessage);
                }
                //Переименовывание магазина
                case Var.storeEdit -> {
                    awaitDao.delete(await);
                    Store store = storeDao.getStore(objId);
                    store.setTitle(update.getMessage().getText());
                    storeDao.saveOrUpdateStore(store);
                    messageBot.sendMessage("Магазин #"+ store.getId() + "  переименован на "+store.getTitle());
                    Message message = sendMenu(userBot.getTgId(),Var.getStoresTitle, CatalogMenu.getStoresList(userBot));
                    //Ползунок последнего сообщения
                    LastMessage lastMessage = lastMessageDAO.getLastMessage(userBot.getId());
                    lastMessage.setLastMessageId(message.getMessageId().longValue());
                    lastMessageDAO.updateLastMessage(lastMessage);
                }
                case Var.catalogCreate -> {
                    ru.masich.bot.entity.Catalog catalog = null;
                    if(objId3 != -1L)
                    {
                        catalog = new ru.masich.bot.entity.Catalog(update.getMessage().getText(),objId,objId3,-1L,null);
                    } else
                    if(objId2 == -1L)
                    {
                        catalog = new ru.masich.bot.entity.Catalog(update.getMessage().getText(),objId,objId2,-1L,null);
                    }

                    catalogDAO.set(catalog);
                    awaitDao.delete(await);
                    messageBot.sendMessage("Каталог " + update.getMessage().getText() + " успешно созданн!");
                    Message message = null;
                    assert catalog != null;
                    ru.masich.bot.entity.Catalog catalog1 = catalogDAO.get(catalog.getFatherId());
                    if(objId2 == -1L)
                        message = sendMenu(userBot.getTgId(),Var.catalogGetMasterTitle, CatalogMenu.getCatalogMenu(objId,objId2,objId3, storeDao.getStore(catalog.getShopId()).getTitle()));
                    else
                        message = sendMenu(userBot.getTgId(),Var.catalogGetChildTitle, CatalogMenu.getCatalogMenu(objId,objId2,objId3, catalog1.getTitle()));

                    LastMessage lastMessage = lastMessageDAO.getLastMessage(userBot.getId());
                    lastMessage.setLastMessageId(message.getMessageId().longValue());
                    lastMessageDAO.updateLastMessage(lastMessage);
                }
                case Var.catalogEdit ->
                {
                    awaitDao.delete(await);
                    ru.masich.bot.entity.Catalog catalog = catalogDAO.get(objId);
                    catalog.setTitle(update.getMessage().getText());
                    catalogDAO.update(catalog);

                    messageBot.sendMessage("Каталог #"+ catalog.getId() + "  переименован на "+catalog.getTitle());

                    Message message = sendMenu(userBot.getTgId(),"Меню каталога: "+catalog.getTitle(), CatalogMenu.getCatalogMenu(catalog.getShopId(), -1L, catalog.getId(), "Каталог "+ catalog.getTitle()));

                    LastMessage lastMessage = lastMessageDAO.getLastMessage(userBot.getId());
                    lastMessage.setLastMessageId(message.getMessageId().longValue());
                    lastMessageDAO.updateLastMessage(lastMessage);
                }
                case Var.productCreate -> {
                    ProductDAO productDAO = new ProductDAOimpl();

                    Map<String, Object> params = new HashMap<>();
                    params.put("title",update.getMessage().getText());
                    Product product = new Product(Math.toIntExact(objId), Math.toIntExact(objId2), params);
                    productDAO.set(product);
                    awaitDao.delete(await);
                    messageBot.sendMessage("Товар " + update.getMessage().getText() + " успешно созданн!");

                    Message message = null;

                    if(objId2 == -1L)
                        message = sendMenu(userBot.getTgId(),Var.catalogGetMasterTitle, CatalogMenu.getCatalogMenu(objId,objId2,objId3, storeDao.getStore(objId).getTitle()));
                    else
                        message = sendMenu(userBot.getTgId(),Var.catalogGetChildTitle, CatalogMenu.getCatalogMenu(objId,objId2,objId3, catalogDAO.get(objId2).getTitle()));


                    LastMessage lastMessage = lastMessageDAO.getLastMessage(userBot.getId());
                    lastMessage.setLastMessageId(message.getMessageId().longValue());
                    lastMessageDAO.updateLastMessage(lastMessage);
                }

                //Если не найденна команда
                default -> {
                    messageBot.sendMessage("Отложенная команда: " + mes + " не распознанна!");
                    messageBot.sendMenu(CatalogMenu.getStartMenu(userBot));
                    awaitDao.delete(await);
                }
            }
            return;
        }
        //Обработка сообщений
        if(update.getMessage() != null)
        {
            messageBot.execute();
        }
        //Обработка кнопок меню
        if(update.getCallbackQuery() != null)
        {
            button.execute(update);
        }
    }

    private void getFrom(Update update)
    {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< getFrom " );
        UserBot userBotTh = null;
        User user = null;

        if(update.getMessage() != null)
            user = update.getMessage().getFrom();
        if(update.getCallbackQuery() != null)
            user = update.getCallbackQuery().getFrom();

        userBotTh = userBotDAO.getUserBot(user);
        //Проверяем если пользователь в базе если есть проверяем изменились ли у него поля, если поменялись, тогда обновляем егов базе
        if(userBotTh == null)
        {
            assert user != null;
            userBotDAO.save(new UserBot(user));
        }
        if(userBotTh != null && !userBotTh.equalsUt(user))
        {
            //Берем данные от сервера телеги
            assert user != null;
            UserBot userBot2 = new UserBot(user);
            //Прописываем id
            userBot2.setId(userBotTh.getId());
            //Сохраняем
            userBotDAO.update(userBot2);
        }
        userBot = userBotTh;
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< getFrom " + userBot.getFirstName() + " " + userBot.getLastName());
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
