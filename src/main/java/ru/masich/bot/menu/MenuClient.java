package ru.masich.bot.menu;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.bot.Client.ClientMessage;
import ru.masich.bot.DAO.CatalogDAO;
import ru.masich.bot.DAO.CatalogDAOimpl;
import ru.masich.bot.DAO.StoreDAOimpl;
import ru.masich.bot.DAO.StoreDao;
import ru.masich.bot.Var;
import ru.masich.bot.entity.Catalog;
import ru.masich.bot.entity.UserBot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuClient {
    private static List<List<InlineKeyboardButton>> storeLines = new ArrayList<>();
    private static CatalogDAO catalogDAO = new CatalogDAOimpl();
    private static StoreDao storeDao = new StoreDAOimpl();

    public static void sendStartMenu(UserBot userBot,String title, int shopID)
    {
        List<Catalog> catalogs = catalogDAO.getCatalogAllStore(Long.valueOf(shopID));

        for (Catalog catalog : catalogs)
        {
            Map<String, Object> params = catalog.getCatalog_atributes();

            var newStore = InlineKeyboardButton.builder()
                    .text(catalog.getTitle())
                    .callbackData(Var.catalogGet + catalog.getId())
                    .build();

            SendPhoto sendPhoto = SendPhoto.builder()
                    .chatId(userBot.getTgId())
                    .photo(new InputFile(params.get("photo") != null ? params.get("photo").toString() : "https://cdn-icons-png.flaticon.com/512/73/73530.png"))
                    .replyMarkup(
                        InlineKeyboardMarkup.builder()
                        .keyboardRow(List.of(newStore))
                                .build())
                    .build();
            try {
                ClientMessage.proxyClient.startBotUser.execute(sendPhoto);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
