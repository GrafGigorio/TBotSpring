package ru.masich.bot.menu;

import com.google.api.client.auth.oauth2.Credential;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.masich.Sheets.Auth;
import ru.masich.Sheets.GoogleSheets;
import ru.masich.bot.DAO.IMPL.*;
import ru.masich.bot.DAO.interfaces.*;
import ru.masich.bot.Var;
import ru.masich.bot.entity.BIgObject;
import ru.masich.bot.entity.Product;
import ru.masich.bot.entity.Store;
import ru.masich.bot.entity.UserBot;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CatalogMenu {
    private static List<List<InlineKeyboardButton>> storeLines = new ArrayList<>();
    private static CatalogDAO catalogDAO = new CatalogDAOimpl();
    private static StoreDao storeDao = new StoreDAOimpl();
    private static UserBotDAO userBotDAO = new UserBotDAOImpl();
    static Logger logger = LogManager.getLogger(CatalogMenu.class);
    public static SendMessage getStartMenu(UserBot userBot)//"("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+
    {
        logger.info("(CatalogMenu.java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< getStartMenu");
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
        logger.info("(CatalogMenu.java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< getStartMenu");
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
        logger.info("(CatalogMenu.java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< getStoresList");
        List<Store> stores = storeDao.getAllUserStores(userBot.getId());
        storeLines = new ArrayList<>();
        for (Store store : stores) {
            List<InlineKeyboardButton> line = new ArrayList<>();
            line.add(
                    InlineKeyboardButton
                            .builder()
                            .text("\uD83D\uDDC2 №" + store.getId() + " " + store.getTitle())
                            .callbackData("store:get:" + store.getId())
                            .build());
            storeLines.add(line);
        }

        storeLines.add(List.of(InlineKeyboardButton.builder().text("◀ Назад").callbackData(Var.startMenu).build()));

        return InlineKeyboardMarkup.builder().keyboard(storeLines)
                .build();
    }
    public static InlineKeyboardMarkup getStoresList(Long userBotId) {
        logger.info("(CatalogMenu.java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< getStoresList");

        Map<String, Object> accesStores = userBotDAO.getUserBot(userBotId).getRole();
        storeLines = new ArrayList<>();

        for (Map.Entry<String, Object> acced : accesStores.entrySet())
        {
            Store store = storeDao.getStore(Long.valueOf(acced.getKey()));
            List<InlineKeyboardButton> line = new ArrayList<>();
            line.add(
                    InlineKeyboardButton
                            .builder()
                            .text("\uD83D\uDDC2 №" + store.getId() + " " + store.getTitle())
                            .callbackData("store:get:" + store.getId())
                            .build());
            storeLines.add(line);
        }

        storeLines.add(List.of(InlineKeyboardButton.builder().text("◀ Назад").callbackData(Var.startMenu).build()));

        return InlineKeyboardMarkup.builder().keyboard(storeLines)
                .build();
    }
    public static InlineKeyboardMarkup getPermission(Long shopId) {
        logger.info("(CatalogMenu.java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< getPermission");

        Map<String, Object> accesUsers = storeDao.getStore(shopId).getRole();

        List<List<InlineKeyboardButton>> but = new ArrayList<>();

        for (Map.Entry<String, Object> acced : accesUsers.entrySet())
        {
            UserBot userBot = userBotDAO.getUserBot(Long.valueOf(acced.getKey()));
            List<InlineKeyboardButton> line = new ArrayList<>();
            line.add(
                    InlineKeyboardButton
                            .builder()
                            .text("\uD83D\uDDC2 №" + userBot.getId() + " " + userBot.getFirstName() + " " + userBot.getLastName())
                            .callbackData("store:user:" + userBot.getId())
                            .build());
            line.add(
                    InlineKeyboardButton
                            .builder()
                            .text("❌")
                            .callbackData("store:delUser:" +shopId +":" + userBot.getId())
                            .build());
            but.add(line);
        }

        but.add(List.of(InlineKeyboardButton.builder().text("◀ Назад").callbackData(Var.storeGet + shopId).build()));

        return InlineKeyboardMarkup.builder().keyboard(but)
                .build();
    }

    public static InlineKeyboardMarkup getStoreMenu(Store store,Long parant, Long catalog)
    {
        logger.info("(CatalogMenu.java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< getStoreMenu");
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

        if(store.getTableID() != null && !store.getTableID().equals("")) {
            storeLines.add(List.of(
                    InlineKeyboardButton
                            .builder()
                            .url("https://docs.google.com/spreadsheets/d/" + store.getTableID() + "/")
                            .text("\uD83D\uDD17 Открыть гугл таблицы \uD83D\uDD17")
                            .build()
            ));
            storeLines.add(List.of(
                    InlineKeyboardButton
                            .builder()
                            .text("⬆\uFE0F Сбросить изменения в таблицах ⬆\uFE0F")
                            .callbackData("table:upload:" + store.getId())
                            .build()
            ));
            storeLines.add(List.of(
                    InlineKeyboardButton
                            .builder()
                            .text("⬇ Принять изменения из гугл таблиц ⬇")
                            .callbackData("table:download:" + store.getId())
                            .build()
            ));
            storeLines.add(List.of(
                    InlineKeyboardButton
                            .builder()
                            .text("✅ Проверить результат загрузки в программу✅")
                            .callbackData("table:check:" + store.getId())
                            .build()
            ));
            storeLines.add(List.of(
                    InlineKeyboardButton
                            .builder()
                            .text("✏Управлять правами на гугл таблицу✏")
                            .callbackData("table:permissions:" + store.getId())
                            .build()
            ));
            storeLines.add(List.of(
                    InlineKeyboardButton
                            .builder()
                            .text("❌ Удалить таблицу ❌")
                            .callbackData("table:delete:" + store.getId())
                            .build()
            ));
        }
        else {
            storeLines.add(List.of(
                    InlineKeyboardButton
                            .builder()
                            .text("➕ Создать гугл таблицу ➕")
                            .callbackData("table:create:" + store.getId())
                            .build()
            ));
        }
        storeLines.add(List.of(
                InlineKeyboardButton
                        .builder()
                        .text("Управление ролями")
                        .callbackData("store:permissions:"+store.getId())
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

    public static InlineKeyboardMarkup getCatalogMenu(Long shopId, Long parentId, Long catalogId,String title)
    {
        logger.info("(CatalogMenu.java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<<  getCatalogMenu");
        //Если указан catalogId то это каталог нижнего уровня в котором должны быть только товары
        //Если указан parentId то это родительский каталог других каталогов
        //Если указан только shopId то это список каталога верхнего уровня
        storeLines = new ArrayList<>();
        List<ru.masich.bot.entity.Catalog> catalogs = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        ProductDAO productDAO = new ProductDAOimpl();

        if(catalogId != -1L)
        {
            catalogs = catalogDAO.getChildren(catalogId);
            products = productDAO.getCatalog(catalogId);
        } else {
            catalogs = catalogDAO.getCatalogAllStore(shopId);
            products = productDAO.getStore(shopId);
        }


        for (ru.masich.bot.entity.Catalog catalog : catalogs)
        {
            storeLines.add(List.of(
                    InlineKeyboardButton
                            .builder()
                            .text("\uD83D\uDDC2 №" + catalog.getId() + " " + catalog.getTitle())
                            .callbackData(Var.catalogGet +catalog.getShopId() + ":" + catalog.getFatherId() + ":" + catalog.getId())
                            .build()
            ));
        }
        for (Product product : products)
        {
            storeLines.add(List.of(
                    InlineKeyboardButton
                            .builder()
                            .text("\uD83E\uDDF8 №" + product.getId() + " " + product.getProductAttributes().get("title"))
                            .callbackData(Var.productGet + product.getId())
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
        storeLines.add(List.of(
                InlineKeyboardButton
                        .builder()
                        .text("Создать товар")
                        .callbackData(Var.productCreate + shopId+":"+catalogId)
                        .build()
        ));
        //Переименовать
        //Удалить

        List<InlineKeyboardButton> line = new ArrayList<>();
        line.add(                InlineKeyboardButton
                .builder()
                .text("✏ Переименовать " + title)
                .callbackData(Var.catalogEdit + catalogId)
                .build());
        line.add(                InlineKeyboardButton
                .builder()
                .text("❌ Удалить " + title)
                .callbackData(Var.catalogDelete + catalogId)
                .build());
        storeLines.add(line);

        if(catalogId != -1L)
            storeLines.add(List.of(
                    InlineKeyboardButton
                            .builder()
                            .text("◀ Назад на уровень")
                            .callbackData(Var.catalogGet+shopId+":"+parentId)
                            .build()
            ));
        else
        if(parentId == -1L)
            storeLines.add(List.of(
                    InlineKeyboardButton
                            .builder()
                            .text("◀ Назад в меню магазина")
                            .callbackData(Var.storeGet+shopId)
                            .build()
            ));

        storeLines.add(List.of(InlineKeyboardButton.builder().text("◀ Назад на главную").callbackData(Var.startMenu).build()));
        return InlineKeyboardMarkup.builder().keyboard(storeLines)
                .build();
    }
    public static InlineKeyboardMarkup getGTablePermissonMenu(Store store) {
        logger.info("(CatalogMenu.java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< getGTablePermissonMenu");

        List<List<InlineKeyboardButton>> but = new ArrayList<>();
        List<Map<String, String>> permissions = new ArrayList<>();
        Credential credential = null;

        BigObjectDAO bigObjectDAO = new BigObjectimpl();

        try {
            credential = Auth.getCredentialsServiceResources();
            permissions = GoogleSheets.getPermissionSpreadsheet(credential, store.getTableID());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        for (Map<String, String> acced : permissions)
        {
            if(acced.get("role").equals("owner"))
                continue;

            BIgObject bIgObject = new BIgObject();
            //bIgObject.setUserId(userId);

            Map<String, Object > obj = new LinkedHashMap<>();
            obj.put("act","table:delPerm:" + acced.get("id") +":"+ store.getTableID()+":"+store.getId());

            bIgObject.setData(obj);

            bigObjectDAO.save(bIgObject);

            List<InlineKeyboardButton> line = new ArrayList<>();
//            line.add(
//                    InlineKeyboardButton
//                            .builder()
//                            .text("✅ " + acced.get("emailAddress"))
//                            .callbackData("table:HZ:")
//                            .build());
            //                    .callbackData("{\"bigObj\":\""+dell.getId()+"\"}")
            line.add(
                    InlineKeyboardButton
                            .builder()
                            .text("❌ " + acced.get("emailAddress") + " ❌")

                            .callbackData("bigObj:"+bIgObject.getId())
                            .build());
            but.add(line);
        }

        but.add(List.of(InlineKeyboardButton.builder().text("➕ Добавить права ➕").callbackData("table:addPerm:"+store.getId()).build()));
        but.add(List.of(InlineKeyboardButton.builder().text("◀ Назад").callbackData(Var.storeGet + store.getId()).build()));

        return InlineKeyboardMarkup.builder().keyboard(but)
                .build();
    }
}
