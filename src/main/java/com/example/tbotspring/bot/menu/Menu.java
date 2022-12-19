package com.example.tbotspring.bot.menu;

import com.example.tbotspring.bot.DAO.*;
import com.example.tbotspring.bot.Proxy;
import com.example.tbotspring.bot.Var;
import com.example.tbotspring.bot.entity.Catalog;
import com.example.tbotspring.bot.entity.Store;
import com.example.tbotspring.bot.entity.UserBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private static List<List<InlineKeyboardButton>> storeLines = new ArrayList<>();
    private static CatalogDAO catalogDAO = new CatalogDAOimpl();
    private static StoreDao storeDao = new StoreDAOimpl();
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

        Store store = storeDao.getStore(storeId);
        storeLines = new ArrayList<>();

        storeLines.add(List.of(
                InlineKeyboardButton
                .builder()
                .text("✏ Каталог")
                .callbackData(Var.catalogGet + store.getId())
                .build()
        ));
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
    public static InlineKeyboardMarkup getStoreMenu(Store store,Long parant, Long catalog)
    {
        storeLines = new ArrayList<>();
        storeLines.add(List.of(
                InlineKeyboardButton
                        .builder()
                        .text("Список разделов")
                        .callbackData(Var.catalogGet+store.getId()+":"+parant+":"+catalog)
                        .build()
        ));

        List<InlineKeyboardButton> line = new ArrayList<>();
        line.add(                InlineKeyboardButton
                .builder()
                .text("✏ Переименовать")
                .callbackData(Var.storeEdit + store.getId())
                .build());
        line.add(                InlineKeyboardButton
                .builder()
                .text("❌ Удалить")
                .callbackData(Var.storeDelete + store.getId())
                .build());
        storeLines.add(line);
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

    public static InlineKeyboardMarkup getCatalogMenu(Long shopId, Long parentId, Long catalogId)
    {
        //Если указан catalogId то это каталог нижнего уровня в котором должны быть только товары
        //Если указан parentId то это родительский каталог других каталогов
        //Если указан только shopId то это список каталога верхнего уровня
        storeLines = new ArrayList<>();
        List<Catalog> catalogs = new ArrayList<>();

        if(catalogId != -1L)
        {
            catalogs = catalogDAO.getChildren(catalogId);
        } else
        if(parentId != -1L)
        {
            catalogs = catalogDAO.getChildren(parentId);
        }
        else
        {
            catalogs = catalogDAO.getCatalogAllStore(shopId);
        }


        for (Catalog catalog : catalogs)
        {
            storeLines.add(List.of(
                    InlineKeyboardButton
                            .builder()
                            .text("#" + catalog.getId() + " " + catalog.getTitle())
                            .callbackData(Var.catalogGet +catalog.getShopId() + ":" + catalog.getFatherId() + ":" + catalog.getId())
                            .build()
            ));
        }
        storeLines.add(List.of(
                InlineKeyboardButton
                        .builder()
                        .text("Создать новый раздел")
                        .callbackData(Var.catalogCreate+shopId+":"+parentId+":"+catalogId)
                        .build()
        ));
        //Переименовать
        //Удалить
        if(parentId == -1L)
            storeLines.add(List.of(
                    InlineKeyboardButton
                            .builder()
                            .text("◀ Назад в меню магазина")
                            .callbackData(Var.storeGet+shopId)
                            .build()
            ));
        else
            storeLines.add(List.of(
                    InlineKeyboardButton
                            .builder()
                            .text("◀ Назад на уровень")
                            .callbackData(Var.catalogGet+shopId+":"+parentId)
                            .build()
            ));
        storeLines.add(List.of(InlineKeyboardButton.builder().text("◀ Назад на главную").callbackData(Var.startMenu).build()));
        return InlineKeyboardMarkup.builder().keyboard(storeLines)
                .build();
    }

}
