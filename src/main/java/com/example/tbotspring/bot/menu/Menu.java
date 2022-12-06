package com.example.tbotspring.bot.menu;

import com.example.tbotspring.bot.Var;
import com.example.tbotspring.bot.entity.UserBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class Menu {
    public static SendMessage getStartMenu(UserBot userBot)
    {

        var newStore = InlineKeyboardButton.builder()
                .text("Добавить новый магазин").callbackData(Var.createStore)
                .build();

        var existStore = InlineKeyboardButton.builder()
                .text("Мои магазины").callbackData(Var.getMyStores)
                .build();

        SendMessage sm = SendMessage.builder().chatId(userBot.getTgId())
                .parseMode("HTML").text(Var.startMenuTitle)
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboardRow(List.of(newStore))
                        .keyboardRow(List.of(existStore))
                        .build()).build();

        return sm;
    }
}
