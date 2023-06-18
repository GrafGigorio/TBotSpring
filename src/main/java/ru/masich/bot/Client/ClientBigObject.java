package ru.masich.bot.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.masich.StartBotUser;
import ru.masich.bot.DAO.IMPL.BigObjectimpl;
import ru.masich.bot.DAO.IMPL.ChartDAOimpl;
import ru.masich.bot.DAO.IMPL.ObjectSendDAOimpl;
import ru.masich.bot.DAO.IMPL.ProductDAOimpl;
import ru.masich.bot.DAO.interfaces.BigObjectDAO;
import ru.masich.bot.DAO.interfaces.ChartDAO;
import ru.masich.bot.DAO.interfaces.ObjectSendDAO;
import ru.masich.bot.DAO.interfaces.ProductDAO;
import ru.masich.bot.ProxyClient;
import ru.masich.bot.entity.*;
import ru.masich.bot.menu.ChartMenu;
import ru.masich.bot.menu.MenuClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Обработка больших запросов которые не влахят в калбек телеги
 * */
public class ClientBigObject {
    static Logger logger = LoggerFactory.getLogger(ClientBigObject.class);
    private final ObjectSendDAO objectSendDAO = new ObjectSendDAOimpl();
    private final ChartDAO chartDAO = new ChartDAOimpl();
    private final ProductDAO productDAO = new ProductDAOimpl();
    private final BigObjectDAO bigObjectDAO = new BigObjectimpl();

    public void process(ProxyClient proxyClient){
        CallbackQuery callbackQuery = proxyClient.startBotUser.update.getCallbackQuery();

        HashMapConverter convert = new HashMapConverter();
        String bigObjectID = (String) convert.convertToEntityAttribute(callbackQuery.getData()).get("bigObj");
        BIgObject bIgObject = bigObjectDAO.get(Integer.parseInt(bigObjectID));
        logger.info("("+this.getClass().getSimpleName()+".java:"
                +new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< process data "
                + callbackQuery.getData() +" " + bIgObject);

        ObjectSend objectSend = objectSendDAO.get(Long.valueOf(bIgObject.getData().get("objId").toString()));
        Long objId = objectSend.getId();



        //Удаление товара из корзины
        if(bIgObject.getData().get("act").equals("chartPrDel")){
            logger.info("("+this.getClass().getSimpleName()+".java:"
                    +new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< Кнопка удаления товара ");
            chartDellLoc(bIgObject,proxyClient.startBotUser, objectSend);
            return;
        }
        //Меню редактирования корзины
        if(bIgObject.getData().get("act").equals("chartSave")){
            logger.info("("+this.getClass().getSimpleName()+".java:"
                    +new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< Кнопка Сохранить при редактировании корзины ");
            new ChartMenu().sendActiveChartEdit(proxyClient.startBotUser, objId);
            return;
        }
        if(bIgObject.getData().get("act").equals("chartProductEdit")){
            logger.info("("+this.getClass().getSimpleName()+".java:"
                    +new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< Кнопка Изменить товар");

            int productId = (int) bIgObject.getData().get("productId");
            int productSelect = (int) bIgObject.getData().get("productSelect");
            int chartID = (int) bIgObject.getData().get("chartID");

            Map<String,Object> chart = new LinkedHashMap<>();
            chart.put("productId",productId+"");
            chart.put("productSelect",productSelect+"");
            chart.put("chartID",chartID+"");
            Product product = productDAO.get(productId);
            MenuClient.sendProductChart(product,objectSend.getUserId(),objectSend.getObjectId(), chart);
            return;
        }
    }
    public void chartDell(StartBotUser startBotUser , ObjectSend objectSend)
    {
        logger.info("("+this.getClass().getSimpleName()+".java:"
                +new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< удаление товара из корзины голобальная функция");
        Map<String, Object> objectMap = (Map<String, Object>) objectSend.getProperty().get("chart");
        //Получаем необходимые атрибуты
        int productId = Integer.parseInt((String) objectMap.get("productId"));
        int productSelect = Integer.parseInt((String) objectMap.get("productSelect"));
        int chartID = Integer.parseInt((String) objectMap.get("chartID"));

        //Получаем корзину для редактирования
        Chart chart = chartDAO.get((long) chartID);

        //Ищем совпадения
        //Получаем продукры корзины
        List<Map<String, Object>> dataChart = (List<Map<String, Object>>) chart.getData().get("products");

        dataChart.removeIf( x ->
        {
            int productIDChart = (int) x.get("productId");
            int selIDChart = (int) x.get("selId");
            return productIDChart == productId && selIDChart == productSelect;
        });
        chartDAO.updateOrAdd(chart);
        new ChartMenu().edit(startBotUser, objectSend);
    }
    public void chartDellLoc(BIgObject bIgObject, StartBotUser startBotUser , ObjectSend objectSend)
    {
        logger.info("("+this.getClass().getSimpleName()+".java:"
                +new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< удаление товара из корзины локальная функция");
        //Получаем необходимые атрибуты
        int productId = Integer.parseInt(bIgObject.getData().get("productId").toString());
        int productSelect = Integer.parseInt(bIgObject.getData().get("productSelect").toString());
        int chartID = Integer.parseInt(bIgObject.getData().get("chartID").toString());

        //Получаем корзину для редактирования
        Chart chart = chartDAO.get((long) chartID);

        //Ищем совпадения
        //Получаем продукры корзины
        List<Map<String, Object>> dataChart = (List<Map<String, Object>>) chart.getData().get("products");

        dataChart.removeIf( x ->
        {
            int productIDChart = (int) x.get("productId");
            int selIDChart = (int) x.get("selId");
            return productIDChart == productId && selIDChart == productSelect;
        });
        chartDAO.updateOrAdd(chart);
        new ChartMenu().edit(startBotUser, objectSend);
    }
}
