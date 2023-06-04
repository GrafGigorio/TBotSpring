package ru.masich.bot.menu;

import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.bot.Client.ClientMessage;
import ru.masich.bot.Client.Func.ObjectSave;
import ru.masich.bot.DAO.IMPL.ChartDAOimpl;
import ru.masich.bot.DAO.IMPL.ProductDAOimpl;
import ru.masich.bot.DAO.interfaces.ChartDAO;
import ru.masich.bot.DAO.interfaces.ProductDAO;
import ru.masich.bot.entity.Chart;
import ru.masich.bot.entity.Product;
import ru.masich.bot.entity.UserBot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChartMenu {
    private static ChartDAO chartDAO = new ChartDAOimpl();
    private static ProductDAO productDAO = new ProductDAOimpl();

    public static void sendActiveChart(UserBot userBot, String title) throws TelegramApiException {

        Chart chart = chartDAO.getActiveChart(userBot.getTgId());
        StringBuilder stringBuilder = new StringBuilder();

        List<Map<String, Object>> products = (List<Map<String, Object>>) chart.getData().get("products");

        List<List<InlineKeyboardButton>> lines = new ArrayList<>();
        List<InlineKeyboardButton> line = new ArrayList<>();
        InlineKeyboardMarkup kb = new InlineKeyboardMarkup();
        for(Map<String, Object> product : products)
        {

            Product product1 = productDAO.get((Integer) product.get("productId"));
            Map<String, Object> prodictAtr = product1.getProductAttributes();

            String selected = (String) ((List<Map<String,Object>>)prodictAtr.get("check_box_prop")).get((Integer) product.get("selId") - 1).get("tit");

            stringBuilder.append(prodictAtr.get("title") + "("+selected+") количество: " + product.get("count") + (product1.getProductAttributes()).get("measurement"));
            stringBuilder.append("\r\n");

        }

        Long objId = ObjectSave.prepare(userBot.getTgId());


        InlineKeyboardButton chartEdit = InlineKeyboardButton.builder()
                .text("Изменить корзину")
                .callbackData(new JSONObject(Map.of("act","chartEdit","objId",objId)).toString())
                .build();
        InlineKeyboardButton chartCommit = InlineKeyboardButton.builder()
                .text("Оформить заказ")
                .callbackData(new JSONObject(Map.of("act","chartCommit","objId",objId)).toString())
                .build();

        lines.add(List.of(chartEdit));
        lines.add(List.of(chartCommit));
        kb.setKeyboard(lines);
        //Приветствие
        ClientMessage.proxyClient.startBotUser.execute(
                SendMessage.builder()
                        .chatId(userBot.getTgId())
                        .text(title)
                        .build());

        ObjectSave.save(objId, userBot.getTgId(), ClientMessage.proxyClient.startBotUser.execute(
                SendMessage.builder()
                        .chatId(userBot.getTgId())
                        .text(stringBuilder.toString())
                        .replyMarkup(kb)
                        .build()).getMessageId());
    }
}
