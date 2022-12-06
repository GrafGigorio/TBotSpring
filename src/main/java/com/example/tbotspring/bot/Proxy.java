package com.example.tbotspring.bot;

import com.example.tbotspring.StartBot;
import com.example.tbotspring.bot.DAO.*;
import com.example.tbotspring.bot.entity.Await;
import com.example.tbotspring.bot.entity.LastMessage;
import com.example.tbotspring.bot.entity.Store;
import com.example.tbotspring.bot.entity.UserBot;
import com.example.tbotspring.bot.menu.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.persistence.Transient;
import java.util.List;

public class Proxy {

    private StoreDao storeDao = new StoreDAOimpl();
    private AwaitDao awaitDao = new AwaitDAOimpl();
    private UserBotDAO userBotDAO = new UserBotDAOImpl();
    private LastMessageDAO lastMessageDAO = new LastMessageDAOimpl();
    private StartBot startBot;
    private Update update;
    private UserBot userBot;

    public Proxy(StartBot startBot) {
        this.startBot = startBot;
    }

    public void proxy(Update update)
    {
        this.update = update;
        getFrom(update);//Сохраняем поле пользователя userBot и проверяем наличие обновлений полей

        //Проверряем наличие отложенных заданий(В случаях где от пользователя ожидается получение названия какого либо обьекта)
        List<Await> awaits = awaitDao.getAll(userBot.getId());

        if(awaits.size() > 0)
        {
            Await await = awaits.get(0);
            String command = await.getCommand();
            //Перечисление отложенных комманд
            switch (command)
            {
                //Создание магазина
                case Var.createStore -> {
                    userBot.setStore(new Store(update.getMessage().getText()));
                    userBotDAO.update(userBot);
                }
                //Если не найденна команда
                default -> {
                    this.sendMessage("Отложенная команда: " + command + " не распознанна!");
                    this.sendMenu(Menu.getStartMenu(userBot));
                }
            }
            awaitDao.delete(await);
            return;
        }
        //Обработка сообщений
        if(update.getMessage() != null)
        {
            LastMessage lastMessage = new LastMessage(userBot,2132L);

            lastMessageDAO.setLastMessage(lastMessage);


            this.sendMessage("Команда: " + update.getMessage().getText() + " не распознанна!");
            this.sendMenu(Menu.getStartMenu(userBot));
        }
        //Обработка кнопок меню
        if(update.getCallbackQuery() != null)
        {
            String mes = update.getCallbackQuery().getMessage().getText();
            switch (mes)
            {
                case Var.createStore -> {
                    Await await = new Await(Var.createStore);
                    this.sendMessage("Введите название нового магазина!");
                }
            }
        }
    }
    private void sendMessage(String mes)
    {
        SendMessage smd = SendMessage.builder().chatId(update.getMessage().getFrom().getId())
                .text(mes).build();
        try {
            startBot.execute(smd);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    private SendMessage sendMenu(SendMessage menu)
    {
        try {
            startBot.execute(menu);
            return menu;
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    private void getFrom(Update update)
    {
        UserBot userBotTh = null;
        User user = null;

        if(update.getMessage() != null)
            user = update.getMessage().getFrom();
        if(update.getCallbackQuery() != null)
            user = update.getCallbackQuery().getFrom();

        userBotTh = userBotDAO.getUserBot(user);
        //Проверяем если пользователь в базе если есть проверяем изменились ли у него поля, если поменялись, тогда обновляем егов базе
        if(userBotTh == null)
        {
            userBotDAO.save(new UserBot(user));
        }
        if(userBotTh != null && !userBotTh.equalsUt(user))
        {
            //Берем данные от сервера телеги
            UserBot userBot2 = new UserBot(user);
            //Прописываем id
            userBot2.setId(userBotTh.getId());
            //Сохраняем
            userBotDAO.update(userBot2);
        }
        userBot = userBotTh;
    }

}
