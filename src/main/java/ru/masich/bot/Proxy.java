package ru.masich.bot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.masich.StartBot;
import ru.masich.bot.DAO.IMPL.*;
import ru.masich.bot.DAO.interfaces.*;
import ru.masich.bot.action.Awaits.AwaitCatalog;
import ru.masich.bot.action.Awaits.AwaitProduct;
import ru.masich.bot.action.Awaits.AwaitStore;
import ru.masich.bot.action.Awaits.AwaitTable;
import ru.masich.bot.action.Button;
import ru.masich.bot.action.MessageBot;
import ru.masich.bot.entity.Await;
import ru.masich.bot.entity.LastMessage;
import ru.masich.bot.entity.UserBot;
import ru.masich.bot.menu.CatalogMenu;

import java.util.ArrayList;
import java.util.List;

public class Proxy {

    private final StoreDao storeDao = new StoreDAOimpl();
    private final AwaitDao awaitDao = new AwaitDAOimpl();
    private final UserBotDAO userBotDAO = new UserBotDAOImpl();
    private final LastMessageDAO lastMessageDAO = new LastMessageDAOimpl();
    private final CatalogDAO catalogDAO = new CatalogDAOimpl();
    private StartBot startBot;
    private Update update;
    private UserBot userBot;

    public Proxy(StartBot startBot) {
        this.startBot = startBot;
    }
    static Logger logger = LogManager.getLogger(Proxy.class);

    public void proxy(Update update)
    {
        String logd= "<< proxy " + (update.getCallbackQuery() != null ? update.getCallbackQuery() : update.getMessage());
        if(logd.length() > 100)
            logd = logd.substring(0,100) + " ...";
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+logd);
        this.update = update;
        getFrom(update);//Сохраняем поле пользователя userBot и проверяем наличие обновлений полей

        //Проверряем наличие отложенных заданий(В случаях где от пользователя ожидается получение названия какого либо обьекта)

        List<Await> awaits = awaitDao.getAll(userBot.getId());
        MessageBot messageBot = new MessageBot(startBot,update,userBot);
        Button button = new Button(startBot,update);

        if(awaits.size() > 0) {
            Await await = awaits.get(0);
            if(update.getMessage() == null)
            {
                messageBot.sendMessage("Отложенная команда: " + await.getCommand() + " не распознанна!");
                Message message = messageBot.sendMenu(CatalogMenu.getStartMenu(userBot));
                LastMessage lastMessage = lastMessageDAO.getLastMessage(userBot.getId());
                lastMessage.setLastMessageId(message.getMessageId());
                lastMessageDAO.updateLastMessage(lastMessage);
                awaitDao.delete(await);
                return;
            }

            List<String> commandSeq = new ArrayList<>(List.of(await.getCommand().split(":")));

            String firstBlock = commandSeq.get(0);
            commandSeq.remove(0);

            if(firstBlock.equals("store")) {
                AwaitStore awaitStore = new AwaitStore(commandSeq, userBot, update, startBot, await);
                awaitStore.execute();
            }
            if(firstBlock.equals("catalog")) {
                AwaitCatalog awaitCatalog = new AwaitCatalog(commandSeq, userBot, update, startBot, await);
                awaitCatalog.execute();
            }
            if(firstBlock.equals("product")) {
                AwaitProduct awaitProduct = new AwaitProduct(commandSeq, userBot, update, startBot, await);
                awaitProduct.execute();
            }
            if(firstBlock.equals("table")) {
                AwaitTable awaitTable = new AwaitTable(commandSeq, userBot, update, startBot, await);
                awaitTable.execute();
            }
            return;
        }
        //Обработка сообщений
        if(update.getMessage() != null)
        {
            messageBot.execute();
        }
        //Обработка кнопок меню
        if(update.getCallbackQuery() != null)
        {
            button.execute(update);
        }
    }

    private void getFrom(Update update)
    {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< getFrom " );
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
            assert user != null;
            userBotDAO.save(new UserBot(user));
        }
        if(userBotTh != null && !userBotTh.equalsUt(user))
        {
            //Берем данные от сервера телеги
            assert user != null;
            UserBot userBot2 = new UserBot(user);
            //Прописываем id
            userBot2.setId(userBotTh.getId());
            //Сохраняем
            userBotDAO.update(userBot2);
        }
        userBot = userBotTh;
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< getFrom " + userBot.getFirstName() + " " + userBot.getLastName());
    }
}
