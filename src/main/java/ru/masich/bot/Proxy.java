package ru.masich.bot;

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
import ru.masich.bot.menu.Menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Proxy {

    private StoreDao storeDao = new StoreDAOimpl();
    private AwaitDao awaitDao = new AwaitDAOimpl();
    private UserBotDAO userBotDAO = new UserBotDAOImpl();
    private LastMessageDAO lastMessageDAO = new LastMessageDAOimpl();
    private CatalogDAO catalogDAO = new CatalogDAOimpl();
    private StartBot startBot;
    private Update update;
    private UserBot userBot;

    public Proxy(StartBot startBot) {
        this.startBot = startBot;
    }

    public void proxy(Update update)
    {
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
                Message message = messageBot.sendMenu(Menu.getStartMenu(userBot));
                LastMessage lastMessage = lastMessageDAO.getLastMessage(userBot.getId());
                lastMessage.setLastMessageId(message.getMessageId());
                lastMessageDAO.updateLastMessage(lastMessage);
                awaitDao.delete(await);
                return;
            }

            Long objId = -1L;
            Long objId2 = -1L;
            Long objId3 = -1L;
            String mes = "";
            String[] comandSeq = await.getCommand().split(":");
            if(comandSeq.length > 2)
            {
                if(comandSeq.length > 3)
                    objId2 = Long.valueOf(comandSeq[3]);
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
                    Store store = new Store(userBot.getId(),update.getMessage().getText());
                    storeDao.saveOrUpdateStore(store);
                    awaitDao.delete(await);
                    messageBot.sendMessage("Магазин " + update.getMessage().getText() + " успешно созданн!");
                    messageBot.sendMenu(Menu.getStartMenu(userBot));
                }
                //Переименовывание магазина
                case Var.storeEdit -> {
                    awaitDao.delete(await);
                    Store store = storeDao.getStore(objId);
                    store.setTitle(update.getMessage().getText());
                    storeDao.saveOrUpdateStore(store);
                    messageBot.sendMessage("Магазин #"+ store.getId() + "  переименован на "+store.getTitle());
                    Message message = sendMenu(userBot.getTgId(),Var.getStoresTitle,Menu.getStoresList(userBot));

                    LastMessage lastMessage = lastMessageDAO.getLastMessage(userBot.getId());
                    lastMessage.setLastMessageId(message.getMessageId().longValue());
                    lastMessageDAO.updateLastMessage(lastMessage);
                }
                case Var.catalogCreate -> {
                    Catalog catalog = null;
                    if(objId3 != -1L)
                    {
                        catalog = new Catalog(update.getMessage().getText(),objId,objId3,-1L,null);
                    } else
                    if(objId2 == -1L)
                    {
                        catalog = new Catalog(update.getMessage().getText(),objId,objId2,-1L,null);
                    }

                    catalogDAO.set(catalog);
                    awaitDao.delete(await);
                    messageBot.sendMessage("Каталог " + update.getMessage().getText() + " успешно созданн!");
                    Message message = null;
                    Catalog catalog1 = catalogDAO.get(catalog.getFatherId());
                    if(objId2 == -1L)
                        message = sendMenu(userBot.getTgId(),Var.catalogGetMasterTitle,Menu.getCatalogMenu(objId,objId2,objId3, storeDao.getStore(catalog.getShopId()).getTitle()));
                    else
                        message = sendMenu(userBot.getTgId(),Var.catalogGetChildTitle,Menu.getCatalogMenu(objId,objId2,objId3, catalog1.getTitle()));

                    LastMessage lastMessage = lastMessageDAO.getLastMessage(userBot.getId());
                    lastMessage.setLastMessageId(message.getMessageId().longValue());
                    lastMessageDAO.updateLastMessage(lastMessage);
                }
                case Var.catalogEdit ->
                {
                    awaitDao.delete(await);
                    Catalog catalog = catalogDAO.get(objId);
                    catalog.setTitle(update.getMessage().getText());
                    catalogDAO.update(catalog);

                    messageBot.sendMessage("Каталог #"+ catalog.getId() + "  переименован на "+catalog.getTitle());

                    Message message = sendMenu(userBot.getTgId(),"Меню каталога: "+catalog.getTitle(), Menu.getCatalogMenu(catalog.getShopId(), -1L, catalog.getId(), "Каталог "+ catalog.getTitle()));

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
                        message = sendMenu(userBot.getTgId(),Var.catalogGetMasterTitle,Menu.getCatalogMenu(objId,objId2,objId3, storeDao.getStore(objId).getTitle()));
                    else
                        message = sendMenu(userBot.getTgId(),Var.catalogGetChildTitle,Menu.getCatalogMenu(objId,objId2,objId3, catalogDAO.get(objId2).getTitle()));


                    LastMessage lastMessage = lastMessageDAO.getLastMessage(userBot.getId());
                    lastMessage.setLastMessageId(message.getMessageId().longValue());
                    lastMessageDAO.updateLastMessage(lastMessage);
                }

                //Если не найденна команда
                default -> {
                    messageBot.sendMessage("Отложенная команда: " + mes + " не распознанна!");
                    messageBot.sendMenu(Menu.getStartMenu(userBot));
                    awaitDao.delete(await);
                }
            }
            return;
        }
        //Обработка сообщений
        if(update.getMessage() != null)
        {
            messageBot.execute(update);
        }
        //Обработка кнопок меню
        if(update.getCallbackQuery() != null)
        {
            button.execute(update);
        }
    }

    private void getFrom(Update update)
    {
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
            userBotDAO.save(new UserBot(user));
        }
        if(userBotTh != null && !userBotTh.equalsUt(user))
        {
            //Берем данные от сервера телеги
            UserBot userBot2 = new UserBot(user);
            //Прописываем id
            userBot2.setId(userBotTh.getId());
            //Сохраняем
            userBotDAO.update(userBot2);
        }
        userBot = userBotTh;
    }

    public Message sendMenu(Long who, String txt, InlineKeyboardMarkup kb) {
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
