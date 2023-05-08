package ru.masich.bot.menu;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.masich.bot.DAO.IMPL.CatalogDAOimpl;
import ru.masich.bot.DAO.IMPL.ProductDAOimpl;
import ru.masich.bot.DAO.IMPL.StoreDAOimpl;
import ru.masich.bot.DAO.interfaces.CatalogDAO;
import ru.masich.bot.DAO.interfaces.ProductDAO;
import ru.masich.bot.DAO.interfaces.StoreDao;
import ru.masich.bot.Var;
import ru.masich.bot.entity.Product;
import ru.masich.bot.entity.Store;
import ru.masich.bot.entity.UserBot;

import java.util.ArrayList;
import java.util.List;

public class CatalogMenu {
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

        List<Store> stores = storeDao.getAllUserStores(userBotId);
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

//        storeLines.add(List.of(
//                InlineKeyboardButton
//                        .builder()
//                        .text("Список товаров")
//                        .callbackData(Var.productGetList+store.getId()+":"+catalog)
//                        .build()
//        ));

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
                        .url("https://docs.google.com/spreadsheets/d/1U-SBrEWiB8jI4ASeiDIQMu75NTWOxadhYCKroVhCQNE/")
                .text("\uD83D\uDD17 Открыть гугл таблицы \uD83D\uDD17")
                .build()
        ));
        storeLines.add(List.of(
                InlineKeyboardButton
                .builder()
                .text("⬆\uFE0F Сбросить изменения в таблицах ⬆\uFE0F")
                .callbackData("store:upload:"+store.getId())
                .build()
        ));
        storeLines.add(List.of(
                InlineKeyboardButton
                .builder()
                .text("⬇\uFE0F Принять изменения из гугл таблиц ⬇\uFE0F")
                .callbackData("store:download:"+store.getId())
                .build()
        ));
        storeLines.add(List.of(
                InlineKeyboardButton
                        .builder()
                        .text("✅\uFE0F Проверить результат загрузки в программу✅\uFE0F")
                        .callbackData("store:check:"+store.getId())
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
}
