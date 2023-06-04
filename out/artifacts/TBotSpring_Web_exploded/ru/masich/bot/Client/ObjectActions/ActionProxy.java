package ru.masich.bot.Client.ObjectActions;

import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.bot.Client.Func.ChartFunc;
import ru.masich.bot.Client.Func.CheckBox;
import ru.masich.bot.DAO.IMPL.ObjectSendDAOimpl;
import ru.masich.bot.DAO.IMPL.ProductDAOimpl;
import ru.masich.bot.DAO.interfaces.ObjectSendDAO;
import ru.masich.bot.DAO.interfaces.ProductDAO;
import ru.masich.bot.ProxyClient;
import ru.masich.bot.entity.ObjectSend;
import ru.masich.bot.entity.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionProxy {
    ObjectSendDAO objectSendDAO = new ObjectSendDAOimpl();
    ProductDAO productDAO = new ProductDAOimpl();
    ProxyClient proxyClient;
    public ActionProxy(ProxyClient proxyClient)
    {
        this.proxyClient = proxyClient;
    }
    public void proxy()
    {
        CallbackQuery callbackQuery = proxyClient.startBotUser.update.getCallbackQuery();

        System.out.println(">>>> ");
        System.out.println(callbackQuery.getData());
        System.out.println(">>>> ");

        JSONObject jsonObject = new JSONObject(callbackQuery.getData());

        ObjectSend objectSend = objectSendDAO.getObject(jsonObject.getLong("objId"));
        int messageId = objectSend.getObjectId();

        if(jsonObject.get("act").equals("sbx")) {
            int productId = (int) objectSend.getProperty().get("productId");
            Product product = productDAO.get(productId);
            Map<String, Object> productAttributes = product.getProductAttributes();
            List<Map<String, Object>> check_box_prop = (List<Map<String, Object>>) productAttributes.get("check_box_prop");

            for (Map<String, Object> checkbox : check_box_prop) {
                if (checkbox.get("id").equals(jsonObject.get("id"))) {
                    checkbox.put("sel", "1");
                } else if (checkbox.containsKey("sel"))
                    checkbox.remove("sel");
            }
            objectSend.getProperty().put("check_box_prop", check_box_prop);

            objectSendDAO.updateObject(objectSend);
            editMessageProductObj(product,objectSend);
            return;
        }
        if(jsonObject.get("act").equals("p")) {
            int productId = (int) objectSend.getProperty().get("productId");
            Product product = productDAO.get(productId);
            Map<String, Object> objectSendProp = objectSend.getProperty();
            int count = objectSendProp.get("count") != null ?
                    (int) objectSendProp.get("count") :
                    jsonObject.getInt("cou");

            count += jsonObject.getInt("cou");
            objectSendProp.put("count", count);

            objectSendDAO.updateObject(objectSend);
            editMessageProductObj(product,objectSend);
            return;
        }
        if(jsonObject.get("act").equals("m")) {
            int productId = (int) objectSend.getProperty().get("productId");
            Product product = productDAO.get(productId);
            Map<String, Object> objectSendProp = objectSend.getProperty();
            int count = objectSendProp.get("count") != null ?
                    (int) objectSendProp.get("count") :
                    jsonObject.getInt("cou");

            count -= jsonObject.getInt("cou");
            objectSendProp.put("count", count);

            objectSendDAO.updateObject(objectSend);
            editMessageProductObj(product,objectSend);
            return;
        }
        //Добавление в корзину
        if(jsonObject.get("act").equals("addChart")) {
            int productId = (int) objectSend.getProperty().get("productId");
            Product product = productDAO.get(productId);
            ChartFunc.add(proxyClient.startBotUser, objectSend, product);
            return;
        }
        //Редактируем корзину
        if(jsonObject.get("act").equals("chartEdit")) {
            ChartFunc.edit(proxyClient.startBotUser, objectSend);
            return;
        }
        //Оформляем заказ
        if(jsonObject.get("act").equals("chartCommit")) {
            return;
        }


    }
    private void setProperty(ObjectSend objectSend, Product product)
    {
        Map<String, Object> productAttributes = product.getProductAttributes();
        Map<String, Object> objectSendProp = objectSend.getProperty();

        productAttributes.put("check_box_prop",objectSendProp.get("check_box_prop"));
    }
    private void editMessageProductObj(Product product, ObjectSend objectSend) {

        Map<String, Object> productAttributes = product.getProductAttributes();
        Map<String, Object> objectSendProperty = objectSend.getProperty();
        int objectId = Math.toIntExact(objectSend.getId());
        Long chatID = proxyClient.startBotUser.update.getCallbackQuery().getFrom().getId();
        String selTitle = "";

        InlineKeyboardMarkup kb = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> count = new ArrayList<>();

            List<Map<String, String>> dsd = (List<Map<String, String>>) productAttributes.get("count_property");
            //Свойства по количеству
            for (Map<String, String> countProp : dsd) {

                countProp.put("objId", objectId + "");
                String title = countProp.get("tit");
                countProp.remove("tit");
                count.add(InlineKeyboardButton.builder()
                        .text(title)
                        .callbackData(new JSONObject(countProp).toString())
                        .build());
            }

        //Добавляем чекбоск
        List<List<InlineKeyboardButton>> lines = new ArrayList<>(CheckBox.check(objectSendProperty, String.valueOf(objectId)));

            //Если пусто берем из продукта
        if(lines.size() == 0)
        {
            lines = new ArrayList<>(CheckBox.check(productAttributes, String.valueOf(objectId)));
        }
        lines.add(count);

        StringBuilder title = new StringBuilder();
        //Добавить в кор зину
        List<Map<String, Object>> checkBox =(List<Map<String, Object>>) objectSendProperty.get("check_box_prop");
        if(checkBox != null) {
            Object sizeTitle = (checkBox).removeIf(x -> {
                if (x.get("sel") != null)
                    title.append(x.get("tit"));
                return false;
            });
        }
        else {
            checkBox =(List<Map<String, Object>>) productAttributes.get("check_box_prop");
            Object sizeTitle = (checkBox).removeIf(x -> {
                if (x.get("sel") != null)
                    title.append(x.get("tit"));
                return false;
            });
        }

        String cuont = objectSendProperty.get("count") == null ? dsd.get(0).get("cou") : objectSendProperty.get("count").toString();

        Map<String,String> callbackAddChart = new HashMap<>();
        callbackAddChart.put("objId", String.valueOf(objectId));
        callbackAddChart.put("act", "addChart");

        InlineKeyboardButton addToChart = InlineKeyboardButton.builder()
                .text("Добавить в корзину: ("+title +") "+ cuont +" " + productAttributes.get("measurement"))
                .callbackData(new JSONObject(callbackAddChart).toString())
                .build();

        lines.add(List.of(addToChart));
        kb.setKeyboard(lines);

        EditMessageReplyMarkup editMessageReplyMarkup = EditMessageReplyMarkup.builder()
                .chatId(chatID)
                .replyMarkup(kb)
                .messageId(objectSend.getObjectId())
                .build();
        try {

            proxyClient.startBotUser.execute(editMessageReplyMarkup);
            //Строка для отключения загрузки на кнопке
            proxyClient.startBotUser.execute(AnswerCallbackQuery.builder().callbackQueryId(proxyClient.startBotUser.update.getCallbackQuery().getId()).build());
        } catch (TelegramApiException d) {
            d.printStackTrace();
        }
    }
}
