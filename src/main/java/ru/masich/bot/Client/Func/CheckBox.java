package ru.masich.bot.Client.Func;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckBox {
    static Logger logger = LogManager.getLogger(CheckBox.class);
    public static List<List<InlineKeyboardButton>> check(Map<String, Object> objectSendProperty, String objectId)
    {
        logger.info("<<  check");
        List<List<InlineKeyboardButton>> lines = new ArrayList<>();
        if(objectSendProperty.get("check_box_prop") != null) {
            Map<String, Map<String, String>> size = (Map<String, Map<String, String>>) objectSendProperty.get("check_box_prop");


            if (size != null) {
                List<InlineKeyboardButton> prop = new ArrayList<>();
                for (Map.Entry<String, Map<String, String>> check_box_prop : size.entrySet()) {

                    Map<String, String> sadsa = check_box_prop.getValue();
                    sadsa.put("objId", objectId);
                    sadsa.put("id", check_box_prop.getKey());
                    String val = sadsa.get("tit");
                    if (sadsa.get("sel") != null) {
                        val = ">" + val + "<";
                        sadsa = new HashMap<>();
                    }
                    //удаляем название так как ограничение на колбэк 64 байта
                    sadsa.remove("tit");
                    String dqweax = new JSONObject(sadsa).toString();
                    prop.add(InlineKeyboardButton.builder()
                            .text(val)
                            .callbackData(dqweax)
                            .build());
                }
                lines.add(prop);
            }
        }
        return lines;
    }
}
