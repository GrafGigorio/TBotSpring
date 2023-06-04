package ru.masich.bot.menu;

import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.bot.Client.ClientButton;
import ru.masich.bot.Client.ClientMessage;
import ru.masich.bot.Client.Func.CheckBox;
import ru.masich.bot.DAO.IMPL.CatalogDAOimpl;
import ru.masich.bot.DAO.IMPL.ObjectSendDAOimpl;
import ru.masich.bot.DAO.IMPL.StoreDAOimpl;
import ru.masich.bot.DAO.interfaces.CatalogDAO;
import ru.masich.bot.DAO.interfaces.ObjectSendDAO;
import ru.masich.bot.DAO.interfaces.StoreDao;
import ru.masich.bot.Var;
import ru.masich.bot.entity.Catalog;
import ru.masich.bot.entity.ObjectSend;
import ru.masich.bot.entity.Product;
import ru.masich.bot.entity.UserBot;

import java.util.*;

public class MenuClient {
    private static List<List<InlineKeyboardButton>> storeLines = new ArrayList<>();
    private static CatalogDAO catalogDAO = new CatalogDAOimpl();
    private static StoreDao storeDao = new StoreDAOimpl();

    public static void sendStartMenu(UserBot userBot,String title, int shopID) throws TelegramApiException {
        //Приветствие
        ClientMessage.proxyClient.startBotUser.execute(
                SendMessage.builder().chatId(userBot.getTgId()).text(title).build());
        ///
        List<Catalog> catalogs = catalogDAO.getCatalogAllStore(Long.valueOf(shopID));
        sendCatalogs(userBot.getTgId(),catalogs);
    }
    public static void sendCatalogs(Long chat_id, List<Catalog> catalogs) throws TelegramApiException {
        for (Catalog catalog : catalogs)
        {
            Map<String, Object> params = catalog.getCatalog_atributes();

            var newStore = InlineKeyboardButton.builder()
                    .text(catalog.getTitle())
                    .callbackData(Var.catalogGet + catalog.getId())
                    .build();

            SendPhoto sendPhoto = SendPhoto.builder()
                    .chatId(chat_id)
                    .photo(new InputFile(
                            params.get("photo") != null ?
                                    params.get("photo").toString() :
                                    "https://cdn-icons-png.flaticon.com/512/73/73530.png"))
                    .replyMarkup(
                            InlineKeyboardMarkup.builder()
                                    .keyboardRow(List.of(newStore))
                                    .build())
                    .build();

            ClientMessage.proxyClient.startBotUser.execute(sendPhoto);
        }
    }
    public static void sendProducts(Long chat_id, List<Product> products) throws TelegramApiException {
        for (Product product : products)
        {
            //Создалем и сохраняем объект в базе для дальнейшей ссылки на него
            ObjectSendDAO objectSendDAO = new ObjectSendDAOimpl();
            ObjectSend objectSend = new ObjectSend(chat_id);
            objectSend.setProperty(Map.of("productId",product.getId()));
            objectSendDAO.updateObject(objectSend);
            //

            objectSend.setObjectId(sendProduct(product, chat_id, objectSend.getId()));
            objectSendDAO.updateObject(objectSend);
        }
    }
    public static int sendProduct(Product product, Long chat_id, Long objectId)
    {
        Map<String, Object> params = product.getProductAttributes();

        List<InlineKeyboardButton> count = new ArrayList<>();

        List<Map<String,String>> dsd = (List<Map<String, String>>) params.get("count_property");
        //Свойства по количеству
        for (Map<String,String> countProp : dsd)
        {
            countProp.put("objId",objectId+"");
            String title = countProp.get("tit");
            countProp.remove("tit");
            count.add(InlineKeyboardButton.builder()
                    .text(title)
                    .callbackData(new JSONObject(countProp).toString())
                    .build());
        }
        //Свойства по размеру
        List<Map<String, Object>> size = (List<Map<String, Object>>) params.get("check_box_prop");

        InlineKeyboardMarkup kb = new InlineKeyboardMarkup();
       //List<List<InlineKeyboardButton>> lines = new ArrayList<>();

        List<List<InlineKeyboardButton>> lines = new ArrayList<>(CheckBox.check(params, String.valueOf(objectId)));

        lines.add(count);

        StringBuilder title = new StringBuilder();
        Object sizeTitle = (size).removeIf(x -> {
            if (x.get("sel") != null)
                title.append(x.get("tit"));
            return false;
        });
        String cuont = dsd.get(0).get("cou");

        Map<String,String> callbackAddChart = new HashMap<>();
        callbackAddChart.put("objId", String.valueOf(objectId));
        callbackAddChart.put("act", "addChart");

        InlineKeyboardButton addToChart = InlineKeyboardButton.builder()
                .text("Добавить в корзину: ("+title +") "+ cuont +" " +params.get("measurement"))
                .callbackData(new JSONObject(callbackAddChart).toString())
                .build();
        ///bot.answer_callback_query(call.id, text="Дата выбрана")

        lines.add(List.of(addToChart));
        kb.setKeyboard(lines);

        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(chat_id)
                .parseMode("HTML")
                .photo(new InputFile(
                        params.get("main_photo") != null ?
                                params.get("main_photo").toString() :
                                "https://cdn-icons-png.flaticon.com/512/73/73530.png"))
                .caption(params.get("title").toString())
                .replyMarkup(kb)
                .build();
        try {
            ClientButton.proxyClient.startBotUser.update.getCallbackQuery();
            return ClientButton.proxyClient.startBotUser.execute(sendPhoto).getMessageId();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
