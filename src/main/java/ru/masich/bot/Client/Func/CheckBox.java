package ru.masich.bot.Client.Func;

import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckBox {
    public static List<List<InlineKeyboardButton>> check(Map<String, Object> objectSendProperty, String objectId)
    {
        List<List<InlineKeyboardButton>> lines = new ArrayList<>();
        if(objectSendProperty.get("check_box_prop") != null) {
            List<Map<String, String>> size = (List<Map<String, String>>) objectSendProperty.get("check_box_prop");


            if (size != null) {
                List<InlineKeyboardButton> prop = new ArrayList<>();
                for (Map<String, String> check_box_prop : size) {
                    check_box_prop.put("objId", objectId + "");
                    String val = check_box_prop.get("tit");
                    if (check_box_prop.get("sel") != null) {
                        val = ">" + val + "<";
                        check_box_prop = new HashMap<>();
                    }
                    //удаляем название так как ограничение на колбэк 64 байта
                    check_box_prop.remove("tit");
                    prop.add(InlineKeyboardButton.builder()
                            .text(val)
                            .callbackData(new JSONObject(check_box_prop).toString())
                            .build());
                }
                lines.add(prop);
            }
        }
        return lines;
    }
}
