package ru.masich;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.bot.ProxyClient;

import java.util.List;

@Component
@PropertySource({"classpath:./application.properties"})
public class StartBotUser extends TelegramLongPollingBot {
    Logger logger = LoggerFactory.getLogger(StartBotUser.class);

    public Update update;

    @Value("${bot.client.username}")
    private String username;

    @Value("${bot.client.token}")
    private String token;

//    public StartBotUser(String username, String token)
//    {
//        this.username = username;
//        this.token = token;
//    }
    @Override
    public void onRegister() {
        super.onRegister();
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("+======++======++======++======++======++======++======++======++======++======++======++======++======++======++======++======++======++======++======++======++======+");
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")<< onUpdateReceived");
        this.update = update;
        ProxyClient proxy = new ProxyClient(this);

        try {
            proxy.proxy();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
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
