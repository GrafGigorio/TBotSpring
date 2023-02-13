package ru.masich.bot.Client.ObjectActions;

import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.masich.bot.DAO.IMPL.ObjectSendDAOimpl;
import ru.masich.bot.DAO.IMPL.ProductDAOimpl;
import ru.masich.bot.DAO.interfaces.ObjectSendDAO;
import ru.masich.bot.DAO.interfaces.ProductDAO;
import ru.masich.bot.ProxyClient;
import ru.masich.bot.entity.ObjectSend;
import ru.masich.bot.entity.Product;

import java.util.List;
import java.util.Map;

public class ActionProxy {
    ObjectSendDAO objectSendDAO = new ObjectSendDAOimpl();
    ProductDAO productDAO = new ProductDAOimpl();
    public void proxy(ProxyClient proxyClient)
    {
        CallbackQuery callbackQuery = proxyClient.startBotUser.update.getCallbackQuery();

        System.out.println(">>>> ");
        System.out.println(callbackQuery.getData());
        System.out.println(">>>> ");

        JSONObject jsonObject = new JSONObject(callbackQuery.getData());

        ObjectSend objectSend = objectSendDAO.getObject(jsonObject.getLong("objId"));
        int messageId = objectSend.getObjectId();
        int productId = (int) objectSend.getProperty().get("productId");
        Product product = productDAO.get(productId);

        if(jsonObject.get("act").equals("sbx")) {
            Map<String, Object> productAttributes = product.getProductAttributes();
            Map<String, Object> objectSendProp = objectSend.getProperty();
            List<Map<String, Object>> check_box_prop = (List<Map<String, Object>>) productAttributes.get("check_box_prop");

            for (Map<String, Object> checkbox : check_box_prop) {
                if (checkbox.get("id").equals(jsonObject.get("id"))) {
                    checkbox.put("sel", "1");
                } else if (checkbox.containsKey("sel"))
                    checkbox.remove("sel");
            }
        }
        if(jsonObject.get("act").equals("p")) {

        }
        if(jsonObject.get("act").equals("m")) {

        }
    }
    private void setProperty(ObjectSend objectSend, Product product)
    {
        Map<String, Object> productAttributes = product.getProductAttributes();
        Map<String, Object> objectSendProp = objectSend.getProperty();

        productAttributes.put("check_box_prop",objectSendProp.get("check_box_prop"));
    }
//    private void editMessage(Long chatId, String queryId, String title, int msgId, InlineKeyboardMarkup menu) {
//        //--msgId;
//        EditMessageText newTxt = EditMessageText.builder()
//                .chatId(chatId.toString())
//                .messageId(msgId).text(title).build();
//
//        EditMessageReplyMarkup newKb = EditMessageReplyMarkup.builder()
//                .replyMarkup(menu)
//                .chatId(chatId.toString()).messageId(msgId).build();
//
//        AnswerCallbackQuery close = AnswerCallbackQuery.builder()
//                .callbackQueryId(queryId).build();
//
//        try {
//            startBot.execute(newTxt);
//            startBot.execute(newKb);
//            startBot.execute(close);
//        } catch (TelegramApiException d) {
//            d.printStackTrace();
//        }
//    }
}
