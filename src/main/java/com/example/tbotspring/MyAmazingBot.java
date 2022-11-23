package com.example.tbotspring;


import com.example.tbotspring.UserBot;
import com.example.tbotspring.dataBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.util.List;
@Component
@Configuration
public class MyAmazingBot extends TelegramLongPollingBot {

    @Value("${bot.username}")
    private String username;

    @Value("${bot.token}")
    private String token;

    @Override
    public void onRegister() {
        super.onRegister();
    }

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        SendMessage message = new SendMessage();
        long chat_id = update.getMessage().getChatId();
        String message_text = "";

        if (update.hasMessage() && update.getMessage().hasText()) {
            if(update.getMessage().getText().equals("/start"))
            {
                UserBot userBot = dataBase.getUser(update.getMessage().getFrom().getId());
                if(userBot == null) {
                    dataBase.saveUser(update.getMessage().getFrom());
                    message_text += "User added\r\n" +
                            "Добавить новы магазин /create_shop \r\n" +
                            "Посмотреть список ваших магазинов get_my_shops";
                }
                else
                {
                    message_text += "\r\nUser Exist\r\n";
                }
            }
            if(update.getMessage().getText().equals("get_my_shops"))
            {

            }



            message.setChatId(chat_id);
            message.setText(message_text);
                   // .setText(message_text);
            try {

                execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
