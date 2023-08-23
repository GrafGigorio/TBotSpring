package ru.masich.bot.action.store;

import com.google.api.client.auth.oauth2.Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.Sheets.Auth;
import ru.masich.Sheets.GoogleSheets;
import ru.masich.StartBot;
import ru.masich.bot.DAO.IMPL.AwaitDAOimpl;
import ru.masich.bot.DAO.IMPL.StoreDAOimpl;
import ru.masich.bot.DAO.interfaces.AwaitDao;
import ru.masich.bot.DAO.interfaces.StoreDao;
import ru.masich.bot.entity.Await;
import ru.masich.bot.entity.Store;
import ru.masich.bot.menu.CatalogMenu;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

public class StoreTable {
    Logger logger = LoggerFactory.getLogger(StoreTable.class);
    StoreDao storeDao = new StoreDAOimpl();
    private List<String> commandSeq;
    private StartBot startBot;
    private Update update;
    private String callbackId;
    long userBotID;

    public StoreTable(List<String> commandSeq, StartBot startBot, Update update, String callbackId, Long userBotID) {
        this.startBot = startBot;
        this.update = update;
        this.callbackId = callbackId;
        this.userBotID = userBotID;
        this.commandSeq = commandSeq;
    }

    public void exec() {
        System.out.println("dddd  " + callbackId + " " + update.getCallbackQuery().getMessage().getMessageId());

        if (commandSeq.get(0).equals("create")) {
            logger.info("(" + this.getClass().getSimpleName() + ".java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                    ")" + "<< create");
            commandSeq.remove(0);
            create();
            return;
        }
        if (commandSeq.get(0).equals("delete")) {
            logger.info("(" + this.getClass().getSimpleName() + ".java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                    ")" + "<< delete");
            commandSeq.remove(0);
            delete();
            return;
        }
        if (commandSeq.get(0).equals("permissions")) {
            logger.info("(" + this.getClass().getSimpleName() + ".java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                    ")" + "<< permissions");
            commandSeq.remove(0);
            permission();
            return;
        }
        if (commandSeq.get(0).equals("addPerm")) {
            logger.info("(" + this.getClass().getSimpleName() + ".java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                    ")" + "<< addPerm");
            commandSeq.remove(0);
            addPerm();
            return;
        }
        if (commandSeq.get(0).equals("delPerm")) {
            logger.info("(" + this.getClass().getSimpleName() + ".java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                    ")" + "<< delPerm");
            commandSeq.remove(0);
            delPerm();
            return;
        }
        if (commandSeq.get(0).equals("upload")) {
            logger.info("(" + this.getClass().getSimpleName() + ".java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                    ")" + "<< upload");
            commandSeq.remove(0);
            upload();
            return;
        }
        if (commandSeq.get(0).equals("download")) {
            logger.info("(" + this.getClass().getSimpleName() + ".java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                    ")" + "<< download");
            commandSeq.remove(0);
            download();
            return;
        }
        if (commandSeq.get(0).equals("check")) {
            logger.info("(" + this.getClass().getSimpleName() + ".java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                    ")" + "<< check");
            commandSeq.remove(0);
            check();
            return;
        }

    }
    //Удаляем права
    private void delPerm(){

        Credential credential = null;
        try {
            credential = Auth.getCredentialsServiceResources();
            GoogleSheets.deletePermissionSpreadsheet(credential, commandSeq.get(0), commandSeq.get(1));
        }
        catch (Exception e){
            e.printStackTrace();
            return;
        }
        commandSeq.set(0,commandSeq.get(2));
        permission();
    }
    //Проверить на изменения
    private void check() {
        Store store = storeDao.getStore(Long.valueOf(commandSeq.get(0)));
        Download download = new Download(store, startBot, update.getCallbackQuery().getId(),update.getCallbackQuery().getFrom().getId());
        download.check();
    }

    //Получить из гугл таблиц
    private void download() {
        Store store = storeDao.getStore(Long.valueOf(commandSeq.get(0)));
        Download download = new Download(store, startBot, update.getCallbackQuery().getId(),update.getCallbackQuery().getFrom().getId());
        download.execute();
        //Обновляем таблицу после изменений нужно для формирования очередности списков
        Upload upload = new Upload(store, startBot, update.getCallbackQuery().getId());
        upload.execute();
    }

    //Отправка в гугл таблицы
    private void upload() {
        Store store = storeDao.getStore(Long.valueOf(commandSeq.get(0)));
        Upload upload = new Upload(store, startBot, update.getCallbackQuery().getId());
        upload.execute();
    }

    private void create() {
        logger.info("(" + this.getClass().getSimpleName() + ".java:" + new Throwable().getStackTrace()[0].getLineNumber() + ") create ");

        Store store = storeDao.getStore(Long.valueOf(commandSeq.get(0)));

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());

        String folderName = store.getId() + "_" + store.getTitle() + "_" + calendar.get(3) + "_" + (calendar.get(2) + 1) + "_" + calendar.get(1);
       // String fileName = store.getId() + "_" + store.getTitle() + "_" + calendar.get(3) + "_" + (calendar.get(2) + 1) + "_" + calendar.get(1) + "_Каталог";
        String fileName = store.getId() + "_" + store.getTitle() + "_" + update.getCallbackQuery().getFrom().getId() + "_Каталог";
        String salesName = store.getId() + "_" + store.getTitle() + "_" + calendar.get(3) + "_" + (calendar.get(2) + 1) + "_" + calendar.get(1) + "_Заказы";

        logger.info("(" + this.getClass().getSimpleName() + ".java:" + new Throwable().getStackTrace()[0].getLineNumber() + ") " + fileName);
        logger.info("(" + this.getClass().getSimpleName() + ".java:" + new Throwable().getStackTrace()[0].getLineNumber() + ") " + salesName);

        //Получаем разрешения гугула
        try {

            logger.info("(" + this.getClass().getSimpleName() + ".java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                    ") Получаем разрешения.");
            Credential credential = Auth.getCredentialsServiceResources();

            logger.info("(" + this.getClass().getSimpleName() + ".java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                    ") Создаем директорию " + folderName);
            store.setFolderID(GoogleSheets.createFolder(credential, folderName));

            logger.info("(" + this.getClass().getSimpleName() + ".java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                    ") Создаем таблицу каталога " + fileName);

            String spTable = GoogleSheets.createSpreadsheet(credential, fileName);
            //Сохраняем идентификатор в базу
            store.setTableID(spTable);
            //Записываем заголовки
            GoogleSheets.createTemplate(spTable);

            logger.info("(" + this.getClass().getSimpleName() + ".java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                    ") Переносим файл "+fileName+" в каталог " + folderName);
            GoogleSheets.moveFileToFolder(credential, store.getTableID(), store.getFolderID());

            storeDao.saveOrUpdateStore(store);

//            EditMessageText newTxt = EditMessageText.builder()
//                .chatId(update.getCallbackQuery().getFrom().getId())
//                .messageId(Integer.valueOf(callbackId)).text(title).build();

            EditMessageReplyMarkup newKb = EditMessageReplyMarkup.builder()
                    .replyMarkup(CatalogMenu.getStoreMenu(store, -1L, -1L))
                    .chatId(update.getCallbackQuery().getFrom().getId()).messageId(update.getCallbackQuery().getMessage().getMessageId()).build();

            startBot.execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(update.getCallbackQuery().getId())
                    .build());

            startBot.execute(newKb);

        } catch (IOException | GeneralSecurityException | TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void delete() {
        Store store = storeDao.getStore(Long.valueOf(commandSeq.get(0)));
        logger.info("(" + this.getClass().getSimpleName() + ".java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                ") Получаем разрешения.");
        Credential credential = null;
        try {
            credential = Auth.getCredentialsServiceResources();
            logger.info("(" + this.getClass().getSimpleName() + ".java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                    ") Удаляем директорию " + store.getFolderID());
            GoogleSheets.delete(credential, store.getFolderID());
            store.setFolderID(null);
            store.setTableID(null);
            store.setChartID(null);

            storeDao.saveOrUpdateStore(store);

            EditMessageReplyMarkup newKb = EditMessageReplyMarkup.builder()
                    .replyMarkup(CatalogMenu.getStoreMenu(store, -1L, -1L))
                    .chatId(update.getCallbackQuery().getFrom().getId()).messageId(update.getCallbackQuery().getMessage().getMessageId()).build();

            startBot.execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(update.getCallbackQuery().getId())
                    .build());

            startBot.execute(newKb);
        } catch (IOException | GeneralSecurityException | TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void permission() {
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

    private void addPerm() {
        Await await = new Await(userBotID, "table:addPerm:" + Long.valueOf(commandSeq.get(0)));
        AwaitDao awaitDao = new AwaitDAOimpl();
        awaitDao.set(await);

        EditMessageText editMessageText = EditMessageText.builder()
                .text("Введите email гугл аккаунта каму необходимо предоставить доступ")
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .chatId(update.getCallbackQuery().getFrom().getId())
                .build();

        try {
            startBot.execute(editMessageText);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

//    private void editMessage(Long chatId, String queryId, String title, int msgId, InlineKeyboardMarkup menu) {
//        //--msgId;
//        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< editMessage 2");
//        EditMessageText newTxt = EditMessageText.builder()
//                .chatId(chatId.toString())
//                .messageId(msgId).text(title).build();
//
//        EditMessageReplyMarkup newKb = EditMessageReplyMarkup.builder()
//                .replyMarkup(menu)
//                .chatId(chatId.toString()).messageId(msgId).build();
//
//        AnswerCallbackQuery close = AnswerCallbackQuery.builder()
//                .callbackQueryId(queryId).build();
//
//        try {
//            startBot.execute(newTxt);
//            startBot.execute(newKb);
//            startBot.execute(close);
//        } catch (TelegramApiException d) {
//            d.printStackTrace();
//        }
//    }
}
