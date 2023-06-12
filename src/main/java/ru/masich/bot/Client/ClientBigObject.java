package ru.masich.bot.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.masich.bot.DAO.IMPL.BigObjectimpl;
import ru.masich.bot.DAO.IMPL.ChartDAOimpl;
import ru.masich.bot.DAO.IMPL.ObjectSendDAOimpl;
import ru.masich.bot.DAO.interfaces.BigObjectDAO;
import ru.masich.bot.DAO.interfaces.ChartDAO;
import ru.masich.bot.DAO.interfaces.ObjectSendDAO;
import ru.masich.bot.ProxyClient;
import ru.masich.bot.entity.BIgObject;
import ru.masich.bot.entity.Chart;
import ru.masich.bot.entity.HashMapConverter;
import ru.masich.bot.entity.ObjectSend;
import ru.masich.bot.menu.ChartMenu;

import java.util.List;
import java.util.Map;

/**
 * Обработка больших запросов которые не влахят в калбек телеги
 * */
public class ClientBigObject {
    static Logger logger = LoggerFactory.getLogger(ClientBigObject.class);
    private static final ObjectSendDAO objectSendDAO = new ObjectSendDAOimpl();
    private static final ChartDAO chartDAO = new ChartDAOimpl();

    public static void process(ProxyClient proxyClient){
        CallbackQuery callbackQuery = proxyClient.startBotUser.update.getCallbackQuery();
        logger.info("(ClientBigObject.java:"
                +new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< process data "
                + callbackQuery.getData());
        BigObjectDAO bigObjectDAO = new BigObjectimpl();
        HashMapConverter convert = new HashMapConverter();
        String bigObjectID = (String) convert.convertToEntityAttribute(callbackQuery.getData()).get("bigObj");
        BIgObject bIgObject = bigObjectDAO.get(Integer.parseInt(bigObjectID));

        ObjectSend objectSend = objectSendDAO.get(Long.valueOf(bIgObject.getData().get("objId").toString()));
        Long objId = objectSend.getId();

        //Удаление товара из корзины
        if(bIgObject.getData().get("act").equals("chartPrDel")){
            //Получаем необходимые атрибуты
            int productId = (int) bIgObject.getData().get("productId");
            int productSelect = (int) bIgObject.getData().get("productSelect");
            int chartID = (int) bIgObject.getData().get("chartID");

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
            new ChartMenu().edit(proxyClient.startBotUser, objectSend);
        }

        if(bIgObject.getData().get("act").equals("chartSave")){
            new ChartMenu().sendActiveChartEdit(proxyClient.startBotUser, objId);
        }
    }
}
