package ru.masich.bot.Client.Func;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.StartBotUser;
import ru.masich.bot.DAO.IMPL.BigObjectimpl;
import ru.masich.bot.DAO.IMPL.ChartDAOimpl;
import ru.masich.bot.DAO.IMPL.ProductDAOimpl;
import ru.masich.bot.DAO.interfaces.BigObjectDAO;
import ru.masich.bot.DAO.interfaces.ChartDAO;
import ru.masich.bot.DAO.interfaces.ProductDAO;
import ru.masich.bot.entity.BIgObject;
import ru.masich.bot.entity.Chart;
import ru.masich.bot.entity.ObjectSend;
import ru.masich.bot.entity.Product;

import java.util.*;

public class ChartFunc {
    static Logger logger = LoggerFactory.getLogger(ChartFunc.class);
    private static final BigObjectDAO bigObjectDAO = new BigObjectimpl();
    public static void add(StartBotUser startBotUser, ObjectSend objectSend, Product product)
    {
        logger.info("<< add");
        ChartDAO chartDAO = new ChartDAOimpl();

        Map<String, Object> productAttributes = product.getProductAttributes();
        Map<String, Object> objectSendProp = objectSend.getProperty();
        Chart chart = chartDAO.getActiveChart(objectSend.getUserId());
        Map<String, Object> chartObjectParams = new HashMap<>();
        List<Map<String, Object>> productList = new ArrayList<>();
        Map<String, Object> productCh = new HashMap<>();
        //Создаем ссылочные обьекты
        if (chart == null) {
            chart = new Chart();
            chart.setUser_id(objectSend.getUserId());
            chartObjectParams.put("products", productList);
            chart.setData(chartObjectParams);
            chart.setChart_active(true);
        } else {
            chartObjectParams = chart.getData();
            productList = (List<Map<String, Object>>) chartObjectParams.get("products");
        }

        int selProductID = -1;
        Map<String, Object> checkBox = (Map<String, Object>) objectSendProp.get("check_box_prop");

        if (checkBox == null) {
            checkBox = (Map<String, Object>) product.getProductAttributes().get("check_box_prop");
        }

        for (Map.Entry<String, Object> sdfsfef : checkBox.entrySet())
        {
            Map<String, Object> x = (Map<String, Object>) sdfsfef.getValue();
            if (x.get("sel") != null)
                selProductID = Integer.parseInt(sdfsfef.getKey());
        }

        String count = objectSendProp.get("count") != null ?
                String.valueOf(objectSendProp.get("count")) :
                ((List<Map<String, String>>) product.getProductAttributes().get("count_property")).get(0).get("count");

        productCh.put("count", count);

        productCh.put("productId", objectSend.getProperty().get("productId") != null ?
                objectSend.getProperty().get("productId") :
                product.getId());

        productCh.put("selId", selProductID);
        productList.add(productCh);

        chartDAO.updateOrAdd(chart);

        try {
            String productTitle = String.valueOf(product.getProductAttributes().get("title"));

            String measurement = String.valueOf(productAttributes.get("measurement"));
            startBotUser.execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(startBotUser.update.getCallbackQuery().getId())
                    .text("Добавленно в корзину -> "+ productTitle+"("+selProductID+") "+count + measurement)
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    public static void edit(StartBotUser startBotUser, ObjectSend objectSend)
    {
        logger.info("<< edit " + objectSend.getObjectId());
        ChartDAO chartDAO = new ChartDAOimpl();

        InlineKeyboardMarkup kb = new InlineKeyboardMarkup();

        Chart chart = chartDAO.getActiveChart(startBotUser.update.getCallbackQuery().getFrom().getId());

        Map<String, Object> chartList = chart.getData();

        List<List<InlineKeyboardButton>> lines = new ArrayList<>();
        List<Map<String, Object>> prs = (List<Map<String, Object>>) chartList.get("products");

        ProductDAO productDAO = new ProductDAOimpl();
        for(Map<String, Object> elem : prs)
        {
            List<InlineKeyboardButton> butLine = new ArrayList<>();
            Product product = productDAO.get((Integer) elem.get("productId"));
            lines.add(List.of(InlineKeyboardButton.builder()
                    .text((String) product.getProductAttributes().get("title"))
                    .callbackData("123")
                    .build()));
            butLine.add(InlineKeyboardButton.builder()
                    .text("➕ Добавить ➕")
                    .callbackData("123")
                    .build());
            butLine.add(InlineKeyboardButton.builder()
                    .text("➖ Убавить ➖")
                    .callbackData("123")
                    .build());




            Map<String, Object> dellDta = new LinkedHashMap<>();
            dellDta.put("objId",objectSend);
            dellDta.put("act","chartPrDel");
            dellDta.put("chartID",chart.getId());
            dellDta.put("productId",elem.get("productId"));
            dellDta.put("productSelect",elem.get("selId"));

            BIgObject dell = new BIgObject(Math.toIntExact(chart.getUser_id()), dellDta);

            bigObjectDAO.save(dell);

            butLine.add(InlineKeyboardButton.builder()
                    .text("❌ Удалить")
                    .callbackData("{\"bigObj\":\""+dell.getId()+"\"}")
                    .build());

            lines.add(butLine);
        }
        Map<String, Object> saveData = new LinkedHashMap<>();
        saveData.put("objId",objectSend);
        saveData.put("act","chartSave");

        BIgObject saveObj = new BIgObject(Math.toIntExact(chart.getUser_id()), saveData);
        bigObjectDAO.save(saveObj);

        lines.add(List.of(InlineKeyboardButton.builder()
                .text("Сохранить")
                .callbackData("{\"bigObj\":\""+saveObj.getId()+"\"}")
                .build()));

        kb.setKeyboard(lines);

        EditMessageReplyMarkup editMessageReplyMarkup = EditMessageReplyMarkup.builder()
                .chatId(startBotUser.update.getCallbackQuery().getFrom().getId())
                .replyMarkup(kb)
                .messageId(objectSend.getObjectId())
                .build();
        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(startBotUser.update.getCallbackQuery().getFrom().getId())
                .messageId(objectSend.getObjectId())
                .text("Редактирование корзины")
                .build();
        try {
            startBotUser.execute(editMessageText);
            startBotUser.execute(editMessageReplyMarkup);

            //Строка для отключения загрузки на кнопке
            startBotUser.execute(AnswerCallbackQuery.builder().callbackQueryId(startBotUser.update.getCallbackQuery().getId()).build());
        } catch (TelegramApiException d) {
            d.printStackTrace();
        }

    }
}
