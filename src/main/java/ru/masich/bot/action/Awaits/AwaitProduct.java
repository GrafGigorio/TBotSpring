package ru.masich.bot.action.Awaits;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.StartBot;
import ru.masich.bot.DAO.IMPL.*;
import ru.masich.bot.DAO.interfaces.*;
import ru.masich.bot.Var;
import ru.masich.bot.action.MessageBot;
import ru.masich.bot.entity.Await;
import ru.masich.bot.entity.LastMessage;
import ru.masich.bot.entity.Product;
import ru.masich.bot.entity.UserBot;
import ru.masich.bot.menu.CatalogMenu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AwaitProduct {
    static Logger logger = LogManager.getLogger(AwaitProduct.class);
    List<String> commandSeq;
    UserBot userBot;
    Update update;
    StartBot startBot;
    Await await;

    public AwaitProduct(List<String> commandSeq, UserBot userBot, Update update, StartBot startBot, Await await) {
        this.commandSeq = commandSeq;
        this.userBot = userBot;
        this.update = update;
        this.startBot = startBot;
        this.await = await;
    }

    public void execute() {
        if (commandSeq.get(0).equals("create")) {
            logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+" create");
            commandSeq.remove(0);

            CatalogDAO catalogDAO = new CatalogDAOimpl();
            StoreDao storeDao = new StoreDAOimpl();
            AwaitDao awaitDao = new AwaitDAOimpl();
            LastMessageDAO lastMessageDAO = new LastMessageDAOimpl();
            MessageBot messageBot = new MessageBot(startBot, update, userBot);

            List<Await> awaits = awaitDao.getAll(userBot.getId());
            Await await = awaits.get(0);

            ProductDAO productDAO = new ProductDAOimpl();

            Map<String, Object> params = new HashMap<>();
            params.put("title", update.getMessage().getText());
            Product product = new Product(Math.toIntExact(Long.parseLong(commandSeq.get(0))), Math.toIntExact(Long.parseLong(commandSeq.get(1))), params);
            productDAO.set(product);
            awaitDao.delete(await);
            messageBot.sendMessage("Товар " + update.getMessage().getText() + " успешно созданн!");

            Message message = null;

            if (Long.parseLong(commandSeq.get(1)) == -1L)
                message = sendMenu(
                        userBot.getTgId(),
                        Var.catalogGetMasterTitle,
                        CatalogMenu.getCatalogMenu(
                                Long.parseLong(commandSeq.get(0)),
                                Long.parseLong(commandSeq.get(1)),
                                commandSeq.size() < 3 ? -1 : Long.parseLong(commandSeq.get(2)),
                                storeDao.getStore(Long.parseLong(commandSeq.get(0))).getTitle()));
            else
                message = sendMenu(
                        userBot.getTgId(),
                        Var.catalogGetChildTitle,
                        CatalogMenu.getCatalogMenu(
                                Long.parseLong(commandSeq.get(0)),
                                Long.parseLong(commandSeq.get(1)),
                                commandSeq.size() < 3 ? -1 : Long.parseLong(commandSeq.get(2)),
                                catalogDAO.get(Long.parseLong(commandSeq.get(1))).getTitle()));


            LastMessage lastMessage = lastMessageDAO.getLastMessage(userBot.getId());
            lastMessage.setLastMessageId(message.getMessageId().longValue());
            lastMessageDAO.updateLastMessage(lastMessage);

        }
    }
    public Message sendMenu(Long who, String txt, InlineKeyboardMarkup kb) {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")<< sendMenu " + txt);
        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("HTML").text(txt)
                .replyMarkup(kb).build();
        try {
            return startBot.execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
