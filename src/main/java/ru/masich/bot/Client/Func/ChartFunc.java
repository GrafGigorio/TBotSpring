package ru.masich.bot.Client.Func;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.StartBotUser;
import ru.masich.bot.DAO.IMPL.ChartDAOimpl;
import ru.masich.bot.DAO.interfaces.ChartDAO;
import ru.masich.bot.entity.Chart;
import ru.masich.bot.entity.ObjectSend;
import ru.masich.bot.entity.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartFunc {
    static Logger logger = LoggerFactory.getLogger(ChartFunc.class);

    public static void add(StartBotUser startBotUser, ObjectSend objectSend, Product product)
    {
        logger.info("(ChartFunc.java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< add");
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


}
