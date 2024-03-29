package ru.masich.bot.Client.ObjectActions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.payments.ShippingQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.bot.Client.ClientBigObject;
import ru.masich.bot.Client.Func.ChartFunc;
import ru.masich.bot.Client.Func.CheckBox;
import ru.masich.bot.DAO.IMPL.ObjectSendDAOimpl;
import ru.masich.bot.DAO.IMPL.ProductDAOimpl;
import ru.masich.bot.DAO.interfaces.ObjectSendDAO;
import ru.masich.bot.DAO.interfaces.ProductDAO;
import ru.masich.bot.ProxyClient;
import ru.masich.bot.entity.ObjectSend;
import ru.masich.bot.entity.Product;
import ru.masich.bot.menu.ChartMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionProxy {
    ObjectSendDAO objectSendDAO = new ObjectSendDAOimpl();
    ProductDAO productDAO = new ProductDAOimpl();
    ProxyClient proxyClient;
    static Logger logger = LogManager.getLogger(ActionProxy.class);
    public ActionProxy(ProxyClient proxyClient)
    {
        this.proxyClient = proxyClient;
    }
    public void proxy()
    {
        CallbackQuery callbackQuery = proxyClient.startBotUser.update.getCallbackQuery();

        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< proxy " + callbackQuery.getData());

        JSONObject jsonObject = new JSONObject(callbackQuery.getData());

        ObjectSend objectSend = objectSendDAO.get(Long.valueOf(jsonObject.getString("objId")));

        if(jsonObject.get("act").equals("sbx")) {
            logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< proxy act sbx");
            int productId = (int) objectSend.getProperty().get("productId");
            Product product = productDAO.get(productId);
            Map<String, Object> productAttributes = product.getProductAttributes();
            Map<String, Map<String, Object>> check_box_prop = (Map<String, Map<String, Object>>) productAttributes.get("check_box_prop");

            for (Map.Entry<String, Map<String, Object>> checkbox : check_box_prop.entrySet()) {
                Map<String, Object> asdawdawdasd = checkbox.getValue();

                if (checkbox.getKey().equals(jsonObject.get("id"))) {
                    asdawdawdasd.put("sel", "1");
                } else if (asdawdawdasd.containsKey("sel"))
                    asdawdawdasd.remove("sel");
            }
            objectSend.getProperty().put("check_box_prop", check_box_prop);

            objectSendDAO.updateObject(objectSend);
            editMessageProductObj(product,objectSend);
            return;
        }
        if(jsonObject.get("act").equals("p")) {
            logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< proxy act p");
            int productId = (int) objectSend.getProperty().get("productId");
            Product product = productDAO.get(productId);
            Map<String, Object> objectSendProp = objectSend.getProperty();
            int count = objectSendProp.get("count") != null ?
                    (int) objectSendProp.get("count") :
                    jsonObject.getInt("cou");
            count += jsonObject.getInt("cou");
            //Добавляем свойство если оно есть затираем
            objectSendProp.put("count", count);

            //Если пустое свойство размера
            if(objectSendProp.get("check_box_prop") == null) {
                Map<String, Map<String, Object>> check_box_prop = (Map<String, Map<String, Object>>) product.getProductAttributes().get("check_box_prop");
                objectSendProp.put("check_box_prop", check_box_prop);
            }

            objectSendDAO.updateObject(objectSend);
            editMessageProductObj(product,objectSend);
            return;
        }
        if(jsonObject.get("act").equals("m")) {
            logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< proxy act m");
            int productId = (int) objectSend.getProperty().get("productId");
            Product product = productDAO.get(productId);
            Map<String, Object> objectSendProp = objectSend.getProperty();
            int count = objectSendProp.get("count") != null ?
                    (int) objectSendProp.get("count") :
                    jsonObject.getInt("cou");

            count -= jsonObject.getInt("cou");
            if(count < 0)
            {
                try {
                    proxyClient.startBotUser.execute(AnswerCallbackQuery.builder()
                            .callbackQueryId(proxyClient.startBotUser.update.getCallbackQuery().getId())
                            .text("Результат "+count+" Мы к сожанению только продаем товар не покупаем))")
                            .build());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            objectSendProp.put("count", count);

            //Если пустое свойство размера
            if(objectSendProp.get("check_box_prop") == null) {
                Map<String, Map<String, Object>> check_box_prop = (Map<String, Map<String, Object>>) product.getProductAttributes().get("check_box_prop");
                objectSendProp.put("check_box_prop", check_box_prop);
            }

            objectSendDAO.updateObject(objectSend);
            editMessageProductObj(product,objectSend);
            return;
        }
        //Добавление в корзину
        if(jsonObject.get("act").equals("addChart")) {
            logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< proxy act addChart");
            int productId = (int) objectSend.getProperty().get("productId");
            Product product = productDAO.get(productId);
            ChartFunc.add(proxyClient.startBotUser, objectSend, product);
            return;
        }
        //Добавление в корзину при ее редактировании
        if(jsonObject.get("act").equals("addChartSave")) {
            logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< proxy act addChartSave");
            int productId = (int) objectSend.getProperty().get("productId");
            Product product = productDAO.get(productId);

            ChartFunc.add(proxyClient.startBotUser, objectSend, product);

            //Удаляем товар с картинкой
            try {
                proxyClient.startBotUser.execute(DeleteMessage
                        .builder()
                        .chatId(objectSend.getUserId())
                        .messageId(Math.toIntExact(objectSend.getObjectId()))
                        .build());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

            ChartMenu chartMenu = new ChartMenu();
            //Отправляем текстовый вид корзины и получаем его id
            Message message = chartMenu.sendActiveChartNew(proxyClient.startBotUser, "");
            objectSend.setObjectId(Long.valueOf(message.getMessageId()));
            objectSendDAO.updateObject(objectSend);
            chartMenu.edit(proxyClient.startBotUser, objectSend);

            //Удаляем старый товар
            ClientBigObject clientBigObject = new ClientBigObject();
            clientBigObject.chartDell(proxyClient.startBotUser, objectSend);
            return;
        }
        //Редактируем корзину
        if(jsonObject.get("act").equals("chartEdit")) {
            logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< proxy act chartEdit");
            logger.info(jsonObject.getInt("objId"));
            new ChartMenu().edit(proxyClient.startBotUser, objectSend);
            return;
        }
        //Оформляем заказ
        if(jsonObject.get("act").equals("chartCommit")) {
            logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< Кнопка офрмить заказ");

            ShippingQuery shippingQuery = new ShippingQuery();
//            shippingQuery.get

            return;
        }

    }
    private void setProperty(ObjectSend objectSend, Product product)
    {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< setProperty");
        Map<String, Object> productAttributes = product.getProductAttributes();
        Map<String, Object> objectSendProp = objectSend.getProperty();

        productAttributes.put("check_box_prop",objectSendProp.get("check_box_prop"));
    }
    private void editMessageProductObj(Product product, ObjectSend objectSend) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< editMessageProductObj");
        Map<String, Object> productAttributes = product.getProductAttributes();
        Map<String, Object> objectSendProperty = objectSend.getProperty();
        int objectId = Math.toIntExact(objectSend.getId());
        Long chatID = proxyClient.startBotUser.update.getCallbackQuery().getFrom().getId();
        String selTitle = "";

        InlineKeyboardMarkup kb = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> count = new ArrayList<>();

            Map<String, Map<String, String>> dsd = (Map<String, Map<String, String>>) productAttributes.get("count_property");
            //Свойства по количеству
            for (Map.Entry<String, Map<String, String>> countProp : dsd.entrySet()) {
                Map<String, String> asd = countProp.getValue();

                asd.put("objId", objectId + "");
                asd.put("id", countProp.getKey());
                String title = asd.get("tit");
                asd.remove("tit");
                String dsdaa = new JSONObject(asd).toString();
                count.add(InlineKeyboardButton.builder()
                        .text(title)
                        .callbackData(dsdaa)
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
        Map<String ,Map<String, Object>> checkBox = (Map<String, Map<String, Object>>) objectSendProperty.get("check_box_prop");

        if(checkBox != null)
            for(Map.Entry<String ,Map<String, Object>> xds : checkBox.entrySet())
            {
                Map<String, Object> x = xds.getValue();
                if (x.get("sel") != null)
                    title.append(x.get("tit"));
            }

        Object cuont = objectSendProperty.get("count") == null ?
                dsd.get("1").get("cou") :
                objectSendProperty.get("count").toString();



        Map<String,String> callbackAddChart = new HashMap<>();
        callbackAddChart.put("objId", String.valueOf(objectId));


        String text = "";
        //Если это карточка корзины
        if(objectSend.getProperty().containsKey("chart")){
            callbackAddChart.put("act", "addChartSave");
            text = "Сохранить ("+title +") "+ cuont +" " +product.getProductAttributes().get("measurement");
        }
        else {
            text = "Добавить в корзину: ("+title +") "+ cuont +" " + productAttributes.get("measurement");
            callbackAddChart.put("act", "addChart");
        }


        InlineKeyboardButton addToChart = InlineKeyboardButton.builder()
                .text(text)
                .callbackData(new JSONObject(callbackAddChart).toString())
                .build();

        lines.add(List.of(addToChart));
        kb.setKeyboard(lines);

        EditMessageReplyMarkup editMessageReplyMarkup = EditMessageReplyMarkup.builder()
                .chatId(chatID)
                .replyMarkup(kb)
                .messageId(Math.toIntExact(objectSend.getObjectId()))
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
