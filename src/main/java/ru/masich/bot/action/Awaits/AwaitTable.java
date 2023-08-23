package ru.masich.bot.action.Awaits;

import com.google.api.client.auth.oauth2.Credential;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.Sheets.Auth;
import ru.masich.Sheets.GoogleSheets;
import ru.masich.StartBot;
import ru.masich.bot.DAO.IMPL.AwaitDAOimpl;
import ru.masich.bot.DAO.IMPL.LastMessageDAOimpl;
import ru.masich.bot.DAO.IMPL.StoreDAOimpl;
import ru.masich.bot.DAO.interfaces.AwaitDao;
import ru.masich.bot.DAO.interfaces.LastMessageDAO;
import ru.masich.bot.DAO.interfaces.StoreDao;
import ru.masich.bot.Validator;
import ru.masich.bot.entity.Await;
import ru.masich.bot.entity.LastMessage;
import ru.masich.bot.entity.Store;
import ru.masich.bot.entity.UserBot;
import ru.masich.bot.menu.CatalogMenu;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class AwaitTable {

    static Logger logger = LogManager.getLogger(AwaitTable.class);
    List<String> commandSeq;
    UserBot userBot;
    Update update;
    StartBot startBot;
    Await await;

    final StoreDao storeDao = new StoreDAOimpl();
    final AwaitDao awaitDao = new AwaitDAOimpl();

    public AwaitTable(List<String> commandSeq, UserBot userBot, Update update, StartBot startBot, Await await) {
        this.commandSeq = commandSeq;
        this.userBot = userBot;
        this.update = update;
        this.startBot = startBot;
        this.await = await;
    }

    public void execute() {
        String mes = update.getMessage().getText();
        Message message = new Message();
        if (commandSeq.get(0).equals("addPerm")) {
            commandSeq.remove(0);
            if (new Validator().emailValidate(mes)) {
                logger.info("("
                        + this.getClass().getSimpleName()
                        + ".java:"
                        + new Throwable().getStackTrace()[0].getLineNumber()
                        + ")" + " addPerm check > " + mes + " OK!");
                Store store = storeDao.getStore(Long.valueOf(commandSeq.get(0)));

                Credential credential = null;
                try {
                    // 'reader', 'commenter', 'writer', 'fileOrganizer', 'organizer', and 'owner'.",
                    credential = Auth.getCredentialsServiceResources();
                    GoogleSheets.setPermissionSpreadsheet(credential,"writer",store.getTableID(),mes);
                } catch (IOException | GeneralSecurityException e) {
                    throw new RuntimeException(e);
                }
                awaitDao.delete(await);
            } else {
                logger.info("("
                        + this.getClass().getSimpleName()
                        + ".java:"
                        + new Throwable().getStackTrace()[0].getLineNumber() + ")"
                        + " addPerm check > " + mes + " Fail!");
                SendMessage smd = SendMessage.builder().chatId(userBot.getTgId())
                        .text("Указан не Email попробуйте еще раз.").build();
                try {
                    message = startBot.execute(smd);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                awaitDao.delete(await);
            }
            message = permissionNew(userBot.getTgId());
            LastMessageDAO lastMessageDAO = new LastMessageDAOimpl();
            LastMessage lastMessage = lastMessageDAO.getLastMessage(userBot.getId());
            lastMessage.setLastMessageId(message.getMessageId());
            lastMessageDAO.updateLastMessage(lastMessage);
        }
        if (commandSeq.get(0).equals("delPerm")) {
            logger.info("(" + this.getClass().getSimpleName() + ".java:" + new Throwable().getStackTrace()[0].getLineNumber() + ")" + " delPerm > " + mes);
            commandSeq.remove(0);

        }
    }
    private void permissionEdit() {
        Store store = storeDao.getStore(Long.valueOf(commandSeq.get(0)));


        EditMessageReplyMarkup newKb = EditMessageReplyMarkup.builder()
                .replyMarkup(CatalogMenu.getGTablePermissonMenu(store))
                .chatId(update.getCallbackQuery().getFrom().getId())
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .build();

        EditMessageText editMessageText = EditMessageText.builder()
                .text("Редактирование прав доступа к таблице: " + store.getTitle())
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .chatId(update.getCallbackQuery().getFrom().getId())
                .build();

        try {
            startBot.execute(editMessageText);
            startBot.execute(newKb);

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    private Message permissionNew(Long chatID) {
        Store store = storeDao.getStore(Long.valueOf(commandSeq.get(0)));

        SendMessage newKb = new SendMessage().builder()
                .replyMarkup(CatalogMenu.getGTablePermissonMenu(store))
                .chatId(chatID)
                .text("Редактирование прав доступа к таблице: " + store.getTitle())
                .build();
        try {
            return startBot.execute(newKb);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
