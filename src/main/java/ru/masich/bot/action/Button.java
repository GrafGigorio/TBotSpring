package ru.masich.bot.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.StartBot;
import ru.masich.bot.DAO.IMPL.*;
import ru.masich.bot.DAO.interfaces.*;
import ru.masich.bot.Var;
import ru.masich.bot.action.store.StoreTable;
import ru.masich.bot.entity.*;
import ru.masich.bot.menu.CatalogMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Button {
    public StartBot startBot;
    private LastMessageDAO lastMessageDAO = new LastMessageDAOimpl();
    private UserBotDAO userBotDAO = new UserBotDAOImpl();
    private AwaitDao awaitDao = new AwaitDAOimpl();
    StoreDao storeDao = new StoreDAOimpl();
    public Update update;

    Logger logger = LoggerFactory.getLogger(Button.class);

    public Button(StartBot startBot, Update update) {
        this.startBot = startBot;
        this.update = update;
    }

    public void execute(Update update) {
        logger.info("(" + this.getClass().getSimpleName() + ".java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                ")" + "<< execute " + update.getCallbackQuery().getData());
        UserBot userBot = userBotDAO.getUserBot(update.getCallbackQuery().getFrom());
        CatalogDAO catalogDAO = new CatalogDAOimpl();

        Long objId = -1L;
        Long objId2 = -1L;
        Long objId3 = -1L;
        String mes = "";
        String[] commandSeqArr = update.getCallbackQuery().getData().split(":");
        List<String> commandSeqList = new ArrayList<>(List.of(commandSeqArr));
        String callbackId = update.getCallbackQuery().getId();

        if (commandSeqList.get(0).equals("bigObj")) {
            commandSeqList.remove(0);

            BigObjectDAO bigObjectDAO = new BigObjectimpl();
            BIgObject bIgObject = bigObjectDAO.get(Integer.parseInt(commandSeqList.get(0)));

            Map<String,Object> param = bIgObject.getData();
            String paramDS = (String) param.get("act");

            bigObjectDAO.delete(bIgObject);

            commandSeqArr = paramDS.split(":");

            commandSeqList = new ArrayList<>(List.of(commandSeqArr));
        }

        if (commandSeqList.get(0).equals("table")) {
            commandSeqList.remove(0);
            StoreTable storeTable = new StoreTable(commandSeqList, startBot, update, callbackId, userBot.getId());
            storeTable.exec();
            return;
        }

        if (commandSeqArr.length > 2) {
            if (commandSeqArr.length > 3)
                objId2 = Long.valueOf(commandSeqArr[3]);
            if (commandSeqArr.length > 4)
                objId3 = Long.valueOf(commandSeqArr[4]);

            if (commandSeqArr[1].equals("table"))
                objId = Long.valueOf(commandSeqArr[3]);
            else
                objId = Long.valueOf(commandSeqArr[2]);
            mes = commandSeqArr[0] + ":" + commandSeqArr[1] + ":";
        } else {
            mes = update.getCallbackQuery().getData();
        }

        Long chatId = update.getCallbackQuery().getFrom().getId();
        LastMessage lastMessage = lastMessageDAO.getLastMessage(userBot.getId());
        int msgId = lastMessage.getLastMessageId().intValue();


        switch (mes) {
            case Var.startMenu -> {
                editMessage(chatId, callbackId, Var.getStartMenuTitle, msgId, CatalogMenu.getStartMenu(userBot.getId()));
            }
            case Var.createStore -> {
                Await await = new Await(userBot.getId(), Var.createStore);
                awaitDao.set(await);
                editMessage(chatId, callbackId, "Введите название нового магазина!", msgId);
            }
            case Var.getMyStores -> {
                editMessage(chatId, callbackId, "Список магазинов", msgId, CatalogMenu.getStoresList(userBot.getId()));
            }
            case Var.storeGet -> {
                Store store = storeDao.getStore(objId);
                editMessage(chatId, callbackId, "Меню магазана " + store.getTitle(), msgId, CatalogMenu.getStoreMenu(store, objId2, objId3));
            }
            case Var.storeEdit -> {
                Await await = new Await(userBot.getId(), Var.storeEdit + objId);
                awaitDao.set(await);
                editMessage(chatId, callbackId, "В ведите новое название магазина", msgId);
            }
            case Var.storeDelete -> {
                Store store = storeDao.getStore(objId);
                Map<String, Object> roles = userBot.getRole();
                roles.remove(String.valueOf(store.getId()));
                userBotDAO.update(userBot);
                storeDao.deleteStore(store);
                editMessage(chatId, callbackId, "Магазин #" + store.getId() + " " + store.getTitle() + " удален!", msgId);
                Message message = sendMenu(userBot.getTgId(), Var.getStoresTitle, CatalogMenu.getStoresList(userBot.getId()));
                lastMessage.setLastMessageId(message.getMessageId());
                lastMessageDAO.updateLastMessage(lastMessage);
            }
            case Var.catalogCreate -> {
                //В данной ситуации objId это id магазина
                //а objId2 это id родительского catalog

                Await await = new Await(userBot.getId(), Var.catalogCreate + objId + ":" + objId2 + ":" + objId3);
                awaitDao.set(await);
                editMessage(chatId, callbackId, "Введите название нового каталога!", msgId);
            }
            case Var.catalogGet -> {
                Store store = storeDao.getStore(objId);
                String title = store.getTitle();
                if (objId3 != -1) {
                    title = "Меню каталога " + catalogDAO.get(objId3).getTitle();
                }
                if (objId2 == -1 && objId3 == -1) {
                    title = "Меню магазина " + storeDao.getStore(objId).getTitle();
                }
                editMessage(chatId, callbackId, title, msgId, CatalogMenu.getCatalogMenu(objId, objId2, objId3, title));
            }
            case Var.catalogEdit -> {
                Await await = new Await(userBot.getId(), Var.catalogEdit + objId);
                awaitDao.set(await);
                editMessage(chatId, callbackId, "В ведите новое название магазина", msgId);
            }
            case Var.catalogDelete -> {
                ru.masich.bot.entity.Catalog catalog = catalogDAO.get(objId);
                Long fat = catalog.getFatherId();
                catalogDAO.delete(catalog);
                ru.masich.bot.entity.Catalog par = catalogDAO.get(fat);
                InlineKeyboardMarkup inlineKeyboardMarkup = null;
                String title = "";
                //Если корневой каталог выводи его содержимое если нет то выводим список на уровень выше
                if (fat == null || fat == -1) {
                    inlineKeyboardMarkup = CatalogMenu.getStoresList(userBot.getId());
                    title = Var.catalogGetMasterTitle + " " + storeDao.getStore(catalog.getShopId()).getTitle();
                } else {
                    inlineKeyboardMarkup = CatalogMenu.getCatalogMenu(par.getShopId(), par.getFatherId(), -1L, par.getTitle());
                    title = Var.getStoresTitle + " " + storeDao.getStore(par.getShopId()).getTitle();
                }
                editMessage(chatId, callbackId, "Каталог #" + catalog.getId() + " " + catalog.getTitle() + " удален!", msgId);
                Message message = sendMenu(
                        userBot.getTgId(),
                        title,
                        inlineKeyboardMarkup);
                lastMessage.setLastMessageId(message.getMessageId());
                lastMessageDAO.updateLastMessage(lastMessage);
            }
            case Var.productCreate -> {
                //В данной ситуации objId это id магазина
                //а objId2 catalog
                Await await = new Await(userBot.getId(), Var.productCreate + objId + ":" + objId2);
                awaitDao.set(await);
                editMessage(chatId, callbackId, "Введите название товара!", msgId);
            }
        }
    }

    private Message sendMessage(String mes) {
        SendMessage smd = SendMessage.builder().chatId(update.getMessage().getFrom().getId())
                .text(mes).build();
        try {
            return startBot.execute(smd);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void editMessage(Long chatId, String queryId, String data, int msgId) {
        logger.info("(" + this.getClass().getSimpleName() + ".java:" + new Throwable().getStackTrace()[0].getLineNumber() + ")" + "<< editMessage " + data);
        EditMessageText newTxt = EditMessageText.builder()
                .chatId(chatId.toString())
                .messageId(msgId).text(data).build();

        EditMessageReplyMarkup newKb = EditMessageReplyMarkup.builder()
                .chatId(chatId.toString()).messageId(msgId).build();

        AnswerCallbackQuery close = AnswerCallbackQuery.builder()
                .callbackQueryId(queryId).build();

        try {
            startBot.execute(newTxt);
            startBot.execute(close);
//            startBot.execute(newTxt);
        } catch (TelegramApiException d) {
            d.printStackTrace();
        }
    }

    private void editMessage(Long chatId, String queryId, String title, int msgId, InlineKeyboardMarkup menu) {
        //--msgId;
        logger.info("(" + this.getClass().getSimpleName() + ".java:" + new Throwable().getStackTrace()[0].getLineNumber() + ")" + "<< editMessage 2");
        EditMessageText newTxt = EditMessageText.builder()
                .chatId(chatId.toString())
                .messageId(msgId).text(title).build();

        EditMessageReplyMarkup newKb = EditMessageReplyMarkup.builder()
                .replyMarkup(menu)
                .chatId(chatId.toString()).messageId(msgId).build();

        AnswerCallbackQuery close = AnswerCallbackQuery.builder()
                .callbackQueryId(queryId).build();

        try {
            startBot.execute(newTxt);
            startBot.execute(newKb);
            startBot.execute(close);
        } catch (TelegramApiException d) {
            d.printStackTrace();
        }
    }

    public Message sendMenu(Long who, String txt, InlineKeyboardMarkup kb) {
        logger.info("(" + this.getClass().getSimpleName() + ".java:" + new Throwable().getStackTrace()[0].getLineNumber() + ")" + "<< sendMenu txt");
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
