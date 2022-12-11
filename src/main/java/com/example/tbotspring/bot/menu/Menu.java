package com.example.tbotspring.bot.menu;

import com.example.tbotspring.bot.DAO.StoreDAOimpl;
import com.example.tbotspring.bot.DAO.StoreDao;
import com.example.tbotspring.bot.DAO.UserBotDAO;
import com.example.tbotspring.bot.DAO.UserBotDAOImpl;
import com.example.tbotspring.bot.Var;
import com.example.tbotspring.bot.entity.Store;
import com.example.tbotspring.bot.entity.UserBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    static List<List<InlineKeyboardButton>> storeLines = new ArrayList<>();
    public static SendMessage getStartMenu(UserBot userBot)
    {
        var newStore = InlineKeyboardButton.builder()
                .text("Добавить новый магазин").callbackData(Var.createStore)
                .build();

        var existStore = InlineKeyboardButton.builder()
                .text("Мои магазины").callbackData(Var.getMyStores)
                .build();

        SendMessage sm = SendMessage.builder().chatId(userBot.getTgId())
                .parseMode("HTML").text(Var.getStartMenuTitle)
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboardRow(List.of(newStore))
                        .keyboardRow(List.of(existStore))
                        .build()).build();
        return sm;
    }
    public static InlineKeyboardMarkup getStartMenu(Long userBot)
    {
        storeLines = new ArrayList<>();
        List<InlineKeyboardButton> lines = new ArrayList<>();

        storeLines.add(
                List.of( InlineKeyboardButton.builder()
                .text("Добавить новый магазин").callbackData(Var.createStore)
                .build())
        );
        storeLines.add(
                List.of( InlineKeyboardButton.builder()
                .text("Мои магазины").callbackData(Var.getMyStores)
                .build())
        );

        return InlineKeyboardMarkup.builder().keyboard(storeLines)
                .build();
    }
    public static InlineKeyboardMarkup getStoresList(UserBot userBot) {
        StoreDao storeDao = new StoreDAOimpl();
        List<Store> stores = storeDao.getAllUserStores(userBot.getId());
        storeLines = new ArrayList<>();
        for (Store store : stores) {
            List<InlineKeyboardButton> line = new ArrayList<>();
            line.add(
                    InlineKeyboardButton
                            .builder()
                            .text("#" + store.getId() + " " + store.getTitle())
                            .callbackData("store:get:" + store.getId())
                            .build());
            storeLines.add(line);
        }

        storeLines.add(List.of(InlineKeyboardButton.builder().text("◀ Назад").callbackData(Var.startMenu).build()));

        return InlineKeyboardMarkup.builder().keyboard(storeLines)
                .build();
    }
    public static InlineKeyboardMarkup getStoresList(Long userBotId) {
        StoreDao storeDao = new StoreDAOimpl();
        List<Store> stores = storeDao.getAllUserStores(userBotId);
        storeLines = new ArrayList<>();
        for (Store store : stores) {
            List<InlineKeyboardButton> line = new ArrayList<>();
            line.add(
                    InlineKeyboardButton
                            .builder()
                            .text("#" + store.getId() + " " + store.getTitle())
                            .callbackData("store:get:" + store.getId())
                            .build());
            storeLines.add(line);
        }

        storeLines.add(List.of(InlineKeyboardButton.builder().text("◀ Назад").callbackData(Var.startMenu).build()));

        return InlineKeyboardMarkup.builder().keyboard(storeLines)
                .build();
    }
    public static InlineKeyboardMarkup getStoreMenu(Long storeId)
    {
        StoreDao storeDao = new StoreDAOimpl();

        Store store = storeDao.getStore(storeId);
        storeLines = new ArrayList<>();

        storeLines.add(List.of(
                InlineKeyboardButton
                .builder()
                .text("✏ Переименовать")
                .callbackData(Var.storeEdit + store.getId())
                .build()
        ));
        storeLines.add(List.of(
                InlineKeyboardButton
                .builder()
                .text("❌ Удалить")
                .callbackData(Var.storeDelete + store.getId())
                .build()
        ));
        storeLines.add(List.of(
                InlineKeyboardButton
                .builder()
                .text("◀ Назад к списку машазинов")
                .callbackData(Var.getMyStores)
                .build()
        ));
        storeLines.add(List.of(InlineKeyboardButton.builder().text("◀ Назад").callbackData(Var.startMenu).build()));
        return InlineKeyboardMarkup.builder().keyboard(storeLines)
                .build();
    }
    public static InlineKeyboardMarkup getStoreMenu(Store store)
    {
        storeLines = new ArrayList<>();

        storeLines.add(List.of(
                InlineKeyboardButton
                .builder()
                .text("✏ Переименовать")
                .callbackData(Var.storeEdit + store.getId())
                .build()
        ));
        storeLines.add(List.of(
                InlineKeyboardButton
                .builder()
                .text("❌ Удалить")
                .callbackData(Var.storeDelete + store.getId())
                .build()
        ));
        storeLines.add(List.of(
                InlineKeyboardButton
                .builder()
                .text("◀ Назад к списку машазинов")
                .callbackData(Var.getMyStores)
                .build()
        ));
        //storeLines.add(List.of(InlineKeyboardButton.builder().text("◀ Назад").callbackData(Var.startMenu).build()));
        return InlineKeyboardMarkup.builder().keyboard(storeLines)
                .build();
    }


}
