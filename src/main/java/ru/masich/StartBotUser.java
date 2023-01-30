package ru.masich;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.masich.bot.ProxyClient;

import java.util.List;

@Component
public class StartBotUser extends TelegramLongPollingBot {


    public Update update;
    @Value("${bot.client.username}")
    private String username;

    @Value("${bot.client.token}")
    private String token;

    @Override
    public void onRegister() {
        super.onRegister();
    }

    @Override
    public void onUpdateReceived(Update update) {
        this.update = update;
        ProxyClient proxy = new ProxyClient(this);
        proxy.proxy();
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
