package ru.masich.bot.action.Awaits;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.StartBot;
import ru.masich.bot.DAO.IMPL.AwaitDAOimpl;
import ru.masich.bot.DAO.IMPL.CatalogDAOimpl;
import ru.masich.bot.DAO.IMPL.LastMessageDAOimpl;
import ru.masich.bot.DAO.IMPL.StoreDAOimpl;
import ru.masich.bot.DAO.interfaces.AwaitDao;
import ru.masich.bot.DAO.interfaces.CatalogDAO;
import ru.masich.bot.DAO.interfaces.LastMessageDAO;
import ru.masich.bot.DAO.interfaces.StoreDao;
import ru.masich.bot.Var;
import ru.masich.bot.action.MessageBot;
import ru.masich.bot.entity.Await;
import ru.masich.bot.entity.LastMessage;
import ru.masich.bot.entity.UserBot;
import ru.masich.bot.menu.CatalogMenu;

import java.util.List;

public class AwaitCatalog {
    static Logger logger = LogManager.getLogger(AwaitCatalog.class);
    List<String> commandSeq;
    UserBot userBot;
    Update update;
    StartBot startBot;
    Await await;

    public AwaitCatalog(List<String> commandSeq, UserBot userBot, Update update, StartBot startBot,Await await) {
        this.commandSeq = commandSeq;
        this.userBot = userBot;
        this.update = update;
        this.startBot = startBot;
        this.await = await;
    }

    public void execute() {
        AwaitDao awaitDao = new AwaitDAOimpl();
        CatalogDAO catalogDAO = new CatalogDAOimpl();
        LastMessageDAO lastMessageDAO = new LastMessageDAOimpl();

        List<Await> awaits = awaitDao.getAll(userBot.getId());
        Await await = awaits.get(0);

        if (commandSeq.get(0).equals("create")) {
            logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+" create");
            commandSeq.remove(0);

            System.out.println("AwaitCatalog dd " + commandSeq);

            ru.masich.bot.entity.Catalog catalog = null;
            if (Long.parseLong(commandSeq.get(2)) != -1L) {
                catalog = new ru.masich.bot.entity.Catalog(
                        update.getMessage().getText(),
                        Long.valueOf(commandSeq.get(0)),
                        Long.valueOf(commandSeq.get(2)),
                        -1L,
                        null);
            } else if (Long.parseLong(commandSeq.get(1)) == -1L) {
                catalog = new ru.masich.bot.entity.Catalog(
                        update.getMessage().getText(),
                        Long.valueOf(commandSeq.get(0)),
                        Long.valueOf(commandSeq.get(2)),
                        -1L,
                        null);
            }
            StoreDao storeDao = new StoreDAOimpl();

            MessageBot messageBot = new MessageBot(startBot, update, userBot);

            catalogDAO.set(catalog);
            awaitDao.delete(await);
            messageBot.sendMessage("Каталог " + update.getMessage().getText() + " успешно созданн!");
            Message message = null;
            assert catalog != null;
            ru.masich.bot.entity.Catalog catalog1 = catalogDAO.get(catalog.getFatherId());
            if (Long.parseLong(commandSeq.get(1)) == -1L)
                message = sendMenu(
                        userBot.getTgId(),
                        Var.catalogGetMasterTitle,
                        CatalogMenu.getCatalogMenu(
                                Long.valueOf(commandSeq.get(0)),
                                Long.valueOf(commandSeq.get(1)),
                                Long.valueOf(commandSeq.get(2)),
                                storeDao.getStore(catalog.getShopId()).getTitle())
                );
            else
                message = sendMenu(
                        userBot.getTgId(),
                        Var.catalogGetChildTitle,
                        CatalogMenu.getCatalogMenu(
                                Long.valueOf(commandSeq.get(0)),
                                Long.valueOf(commandSeq.get(1)),
                                Long.valueOf(commandSeq.get(2)),
                                catalog1.getTitle()));

            LastMessage lastMessage = lastMessageDAO.getLastMessage(userBot.getId());
            lastMessage.setLastMessageId(message.getMessageId().longValue());
            lastMessageDAO.updateLastMessage(lastMessage);


        }
        if (commandSeq.get(0).equals("edit")) {
            logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+" edit");
            commandSeq.remove(0);

            awaitDao.delete(await);
            ru.masich.bot.entity.Catalog catalog = catalogDAO.get(commandSeq.get(0));
            catalog.setTitle(update.getMessage().getText());
            catalogDAO.update(catalog);

            MessageBot messageBot = new MessageBot(startBot, update, userBot);
            messageBot.sendMessage("Каталог #" + catalog.getId() + "  переименован на " + catalog.getTitle());

            Message message = sendMenu(userBot.getTgId(), "Меню каталога: " + catalog.getTitle(), CatalogMenu.getCatalogMenu(catalog.getShopId(), -1L, catalog.getId(), "Каталог " + catalog.getTitle()));

            LastMessage lastMessage = lastMessageDAO.getLastMessage(userBot.getId());
            lastMessage.setLastMessageId(message.getMessageId().longValue());
            lastMessageDAO.updateLastMessage(lastMessage);

        }
    }

    public Message sendMenu(Long who, String txt, InlineKeyboardMarkup kb) {
        logger.info("(" + this.getClass().getSimpleName() + ".java:" + new Throwable().getStackTrace()[0].getLineNumber() + ")<< sendMenu " + txt);
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
