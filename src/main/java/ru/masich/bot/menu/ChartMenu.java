package ru.masich.bot.menu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.StartBotUser;
import ru.masich.bot.Client.ClientButton;
import ru.masich.bot.Client.ClientMessage;
import ru.masich.bot.Client.Func.ObjectSave;
import ru.masich.bot.DAO.IMPL.BigObjectimpl;
import ru.masich.bot.DAO.IMPL.ChartDAOimpl;
import ru.masich.bot.DAO.IMPL.ObjectSendDAOimpl;
import ru.masich.bot.DAO.IMPL.ProductDAOimpl;
import ru.masich.bot.DAO.interfaces.BigObjectDAO;
import ru.masich.bot.DAO.interfaces.ChartDAO;
import ru.masich.bot.DAO.interfaces.ObjectSendDAO;
import ru.masich.bot.DAO.interfaces.ProductDAO;
import ru.masich.bot.entity.BIgObject;
import ru.masich.bot.entity.Chart;
import ru.masich.bot.entity.ObjectSend;
import ru.masich.bot.entity.Product;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChartMenu {
    private final ChartDAO chartDAO = new ChartDAOimpl();
    private final ProductDAO productDAO = new ProductDAOimpl();
    private final BigObjectDAO bigObjectDAO = new BigObjectimpl();
    private final ObjectSendDAO objectSendDAO = new ObjectSendDAOimpl();
     Logger logger = LogManager.getLogger(ChartMenu.class);
    private StringBuilder stringBuilder = new StringBuilder();
    Long objId = 0L;
    Long chatID = 0L;
    Long prepareObject = 0L;
    InlineKeyboardMarkup kb = new InlineKeyboardMarkup();
    //send new
    public void sendActiveChartNew(StartBotUser userBot, String title)  {
        updateVars(userBot,true);
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< sendActiveChartNew");


        //Приветствие


        int message = 0;
        try {
            ClientMessage.proxyClient.startBotUser.execute(
                    SendMessage.builder()
                            .chatId(chatID)
                            .text(title)
                            .build());
            message = userBot.execute(
                    SendMessage.builder()
                            .chatId(chatID)
                            .text(stringBuilder.toString())
                            .replyMarkup(kb)
                            .build()).getMessageId();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        ObjectSave.save(prepareObject, chatID, (long) message);

    }
    //edit
    public void sendActiveChartEdit(StartBotUser userBot, Long updateObject) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< sendActiveChartEdit updateObject "+ updateObject);

        prepareObject = updateObject;
        updateVars(userBot);

        Long dq = objectSendDAO.get(updateObject).getObjectId();

        EditMessageReplyMarkup editMessageReplyMarkup = EditMessageReplyMarkup.builder()
                .chatId(chatID)
                .replyMarkup(kb)
                .messageId(Math.toIntExact(dq))
                .build();
        EditMessageText editMessageText = EditMessageText.builder()
                .text(stringBuilder.toString())
                .messageId(Math.toIntExact(dq))
                .chatId(chatID).build();


        try {
            ClientButton.proxyClient.startBotUser.execute(editMessageText);
            ClientButton.proxyClient.startBotUser.execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }


    }
    private void updateVars(StartBotUser userBot, Boolean prepare)
    {
        prepareObject = ObjectSave.prepare(chatID);
        updateVars(userBot);
    }
    private void updateVars(StartBotUser userBot)
    {
        logger.info("(ChartMenu.java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< sendActiveChart");


        if(userBot.update.hasCallbackQuery())
            chatID = userBot.update.getCallbackQuery().getFrom().getId();
        else if (userBot.update.hasMessage()) {
            chatID = userBot.update.getMessage().getFrom().getId();
        }

        Chart chart = chartDAO.getActiveChart(chatID);

        List<Map<String, Object>> products = (List<Map<String, Object>>) chart.getData().get("products");

        List<List<InlineKeyboardButton>> lines = new ArrayList<>();

        for(Map<String, Object> product : products)
        {

            Product product1 = productDAO.get((Integer) product.get("productId"));
            Map<String, Object> prodictAtr = product1.getProductAttributes();

            Map<String , Object> checks = (Map<String, Object>) prodictAtr.get("check_box_prop");
            //   .get((Integer) product.get("selId") - 1).get("tit");
            String selected = "";
            for(Map.Entry<String , Object> ddas : checks.entrySet())
            {
                Map<String , Object> dsq = (Map<String, Object>) ddas.getValue();
                if(dsq.get("sel") != null)
                {
                    selected = (String) dsq.get("tit");
                }
            }
            stringBuilder.append(prodictAtr.get("title") + "("+selected+") количество: " + product.get("count") + (product1.getProductAttributes()).get("measurement"));
            stringBuilder.append("\r\n");

        }

        JSONObject chartEditJ = new JSONObject(Map.of("act","chartEdit","objId",prepareObject+""));
        JSONObject chartCommitJ = new JSONObject(Map.of("act","chartCommit","objId",prepareObject+""));

        logger.info("(ChartMenu.java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< callbackData chartEditJ " + chartEditJ);
        logger.info("(ChartMenu.java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< callbackData chartCommitJ " + chartCommitJ);

        InlineKeyboardButton chartEdit = InlineKeyboardButton.builder()
                .text("Изменить корзину")
                .callbackData(chartEditJ.toString())
                .build();
        InlineKeyboardButton chartCommit = InlineKeyboardButton.builder()
                .text("Оформить заказ")
                .callbackData(chartCommitJ.toString())
                .build();

        lines.add(List.of(chartEdit));
        lines.add(List.of(chartCommit));
        kb.setKeyboard(lines);
    }
    public void edit(StartBotUser startBotUser, ObjectSend objectSend)
    {
        logger.info("<< edit " + objectSend);

        BigObjectDAO bigObjectDAO = new BigObjectimpl();
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
                    .text("✏\uFE0F Изменить ✏\uFE0F")
                    .callbackData("123")
                    .build());

            Map<String, Object> dellDta = new LinkedHashMap<>();
            dellDta.put("objId",objectSend.getId());
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
        saveData.put("objId",objectSend.getId());
        saveData.put("act","chartSave");

        BIgObject saveBigObj = new BIgObject(Math.toIntExact(chart.getUser_id()), saveData);
        bigObjectDAO.save(saveBigObj);

        lines.add(List.of(InlineKeyboardButton.builder()
                .text("Сохранить")
                .callbackData("{\"bigObj\":\""+saveBigObj.getId()+"\"}")
                .build()));

        kb.setKeyboard(lines);

        EditMessageReplyMarkup editMessageReplyMarkup = EditMessageReplyMarkup.builder()
                .chatId(startBotUser.update.getCallbackQuery().getFrom().getId())
                .replyMarkup(kb)
                .messageId(Math.toIntExact(objectSend.getObjectId()))
                .build();
        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(startBotUser.update.getCallbackQuery().getFrom().getId())
                .messageId(Math.toIntExact(objectSend.getObjectId()))
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
