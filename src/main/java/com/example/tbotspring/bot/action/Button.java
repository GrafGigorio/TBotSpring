package com.example.tbotspring.bot.action;

import com.example.tbotspring.StartBot;
import com.example.tbotspring.bot.DAO.*;
import com.example.tbotspring.bot.Proxy;
import com.example.tbotspring.bot.Var;
import com.example.tbotspring.bot.entity.Await;
import com.example.tbotspring.bot.entity.LastMessage;
import com.example.tbotspring.bot.entity.Store;
import com.example.tbotspring.bot.entity.UserBot;
import com.example.tbotspring.bot.menu.Menu;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.xml.catalog.Catalog;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Button {
    private StartBot startBot;
    private LastMessageDAO lastMessageDAO = new LastMessageDAOimpl();
    private UserBotDAO userBotDAO = new UserBotDAOImpl();
    private AwaitDao awaitDao = new AwaitDAOimpl();
    StoreDao storeDao = new StoreDAOimpl();
    private  Update update;

    public Button(StartBot startBot, Update update) {
        this.startBot = startBot;
        this.update = update;
    }

    public void execute(Update update)
    {
        UserBot userBot = userBotDAO.getUserBot(update.getCallbackQuery().getFrom());


        Long objId = -1L;
        Long objId2 = -1L;
        Long objId3 = -1L;
        String mes = "";
        String[] comandSeq = update.getCallbackQuery().getData().split(":");
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
            mes = update.getCallbackQuery().getData();
        }

        Long chatId = update.getCallbackQuery().getFrom().getId();
        LastMessage lastMessage = lastMessageDAO.getLastMessage(userBot.getId());
        int msgId = lastMessage.getLastMessageId().intValue();
        String callbackId = update.getCallbackQuery().getId();


        switch (mes)
        {
            case Var.startMenu -> {
                editMessage(chatId,callbackId,Var.getStartMenuTitle,msgId, Menu.getStartMenu(userBot.getId()));
            }
            case Var.createStore -> {
                Await await = new Await(userBot.getId(), Var.createStore);
                awaitDao.set(await);
                editMessage(chatId,callbackId,"Введите название нового магазина!",msgId);
            }
            case Var.getMyStores -> {
                editMessage(chatId,callbackId,"Список магазинов",msgId, Menu.getStoresList(userBot.getId()));
            }
            case Var.storeGet -> {
                Store store = storeDao.getStore(objId);
                editMessage(chatId,callbackId,"Меню магазана " + store.getTitle(),msgId, Menu.getStoreMenu(store,objId2,objId3));
            }
            case Var.storeEdit -> {
                Await await = new Await(userBot.getId(), Var.storeEdit + objId );
                awaitDao.set(await);
                editMessage(chatId,callbackId,"В ведите новое название магазина",msgId);
            }
            case Var.storeDelete -> {
                Store store = storeDao.getStore(objId);
                storeDao.deleteStore(store);
                editMessage(chatId,callbackId,"Магазин #"+store.getId() +" "+store.getTitle() + " удален!", msgId);
                Message message = sendMenu(userBot.getTgId(),Var.getStoresTitle,Menu.getStoresList(userBot.getId()));
                lastMessage.setLastMessageId(message.getMessageId());
                lastMessageDAO.updateLastMessage(lastMessage);
            }
            case Var.catalogGet -> {
                Store store = storeDao.getStore(objId);
                editMessage(chatId,callbackId,"Меню каталога " + store.getTitle(),msgId, Menu.getCatalogMenu(objId,objId2,objId3));
            }
            case Var.catalogCreate -> {
                //В данной ситуации objId это id магазина
                //а objId2 это id родительского catalog

                Await await = new Await(userBot.getId(), Var.catalogCreate+objId+":"+objId2+":"+objId3);
                awaitDao.set(await);
                editMessage(chatId,callbackId,"Введите название нового каталога!",msgId);
            }
        }
    }

    private Message sendMessage(String mes)
    {
        SendMessage smd = SendMessage.builder().chatId(update.getMessage().getFrom().getId())
                .text(mes).build();
        try {
            return startBot.execute(smd);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void editMessage(Long chatId, String queryId, String data, int msgId) {

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
