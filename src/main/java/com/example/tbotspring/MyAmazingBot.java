package com.example.tbotspring;


import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.util.*;

@Component
@Configuration
public class MyAmazingBot extends TelegramLongPollingBot {

    final String createStore = "/create_shop";
    final String getMyStores = "/get_my_shops";
    final String startMenu = "/start_mynu";
    final String storeCommand = "store";
    final String storeEdit = "store:edit";


    //        var next = InlineKeyboardButton.builder()
//                .text("Next").callbackData("next")
//                .build();
//
//        var back = InlineKeyboardButton.builder()
//                .text("Back").callbackData("back")
//                .build();
//
//        var url = InlineKeyboardButton.builder()
//                .text("Tutorial")
//                .url("https://core.telegram.org/bots/api")
//                .build();


    private Map<Long, String> owait = new HashMap<>();
    Message lastMessage;
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

        SendMessage sendMessage = new SendMessage();
        Message message = update.getMessage();
        CallbackQuery callbackQuery = update.getCallbackQuery();


        long chat_id = message != null ? message.getChatId() : callbackQuery.getFrom().getId();

        ///Если еть ожидающие сообщения
        if (owait.size() > 0 && owait.containsKey(update.getMessage().getFrom().getId()))
        {
            sendMessage.setChatId(chat_id);
            String mesage = owait.remove(update.getMessage().getFrom().getId());
            if(mesage.equals(createStore))
            {
                createStore(dataBase.getUser(update.getMessage().getFrom().getId()), update.getMessage().getText());

                sendMessage.setText("Магазин '" + update.getMessage().getText() + "' успешно создан!");

                try {

                    execute(sendMessage); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                lastMessage = sendMenu(update.getMessage().getChatId(),"Start menu", getMyStores(update.getMessage().getFrom().getId()));
                return;
            }
            if(mesage.contains(storeEdit))
            {
                Store store = Store.getStore(Long.valueOf(mesage.split(":")[2]));
                UserBot userBot = dataBase.getUser(update.getMessage().getFrom().getId());

                store.setTitle(update.getMessage().getText());

                userBot.updateStore(store);
                sendMessage.setText("Магазин '" + store.getId() +" "+ store.getTitle() + "' успешно переименованн!");

                try {

                    execute(sendMessage); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                lastMessage = sendMenu(update.getMessage().getChatId(),"Start menu", getMyStores(update.getMessage().getFrom().getId()));
                return;

            }
        }

        if(callbackQuery != null) {
            buttonTap(chat_id, callbackQuery.getId(), callbackQuery.getData(), lastMessage.getMessageId());
        }
        if(message != null)
        {
            lastMessage = sendMenu(update.getMessage().getChatId(),"Start menu", getStartMenu());
        }



////Buttons are wrapped in lists since each keyboard is a set of button rows
//        keyboardM2 = InlineKeyboardMarkup.builder()
//                .keyboardRow(List.of(back))
//                .keyboardRow(List.of(url))
//                .build();

//
//
//
//
//
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            if(update.getMessage().getText().equals("/start"))
//            {
//                UserBot userBot = dataBase.getUser(update.getMessage().getFrom().getId());
//                System.out.println(userBot);
//                if(userBot == null) {
//                    userBot = dataBase.getUser(update.getMessage().getFrom().getId());
//                    dataBase.saveUser(userBot);
//                    message_text += "User added\r\n" + mes;
//
//                }
//                else
//                {
//                    message_text += "User Exist "+mes;
//                }
//            }
//            if(update.getMessage().getText().equals(createStore))
//            {
//                UserBot userBot = dataBase.getUser(update.getMessage().getFrom().getId());
//                owait.put(userBot.getTgId(), createStore);
//                sendMessage.setChatId(chat_id);
//                sendMessage.setText("Введите название магазина: ");
//                try {
//
//                    execute(sendMessage); // Sending our message object to user
//                } catch (TelegramApiException e) {
//                    e.printStackTrace();
//                }
//                return;
//            }
//            if(update.getMessage().getText().equals(getMyStores))
//            {
//                UserBot userBot = dataBase.getUser(update.getMessage().getFrom().getId());
//                List<Store> stores = userBot.getStores();
//
//                for (Store store: stores)
//                {
//                    message_text += store.getTitle() + "\r\n";
//                }
//                if(message_text.length() == 0)
//                    message_text = "Магазинов не найденно";
//            }
//
//
//            if(message_text.length() == 0) {
//                sendMessage.setChatId(chat_id);
//                sendMessage.setText("Не опознанно \r\n" + mes);
//            }
//            else
//            {
//                sendMessage.setChatId(chat_id);
//                sendMessage.setText(message_text);
//            }
//                   // .setText(message_text);
//            try {
//                sendMessage.setText(message_text + update.getUpdateId());
//                execute(sendMessage); // Sending our message object to user
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
//        }
    }
    private InlineKeyboardMarkup getStartMenu()
    {

        var newStore = InlineKeyboardButton.builder()
                .text("Добавить новый магазин").callbackData(createStore)
                .build();

        var existStore = InlineKeyboardButton.builder()
                .text("Мои магазины").callbackData(getMyStores)
                .build();

//        var url = InlineKeyboardButton.builder()
//                .text("Tutorial")
//                .url("https://core.telegram.org/bots/api")
//                .build();

        return InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(newStore))
                .keyboardRow(List.of(existStore))
                .build();
    }

    public Message sendMenu(Long who, String txt, InlineKeyboardMarkup kb){
        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("HTML").text(txt)
                .replyMarkup(kb).build();
        try {
            return execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    private void buttonTap(Long id, String queryId, String data, int msgId) {

        EditMessageText newTxt = EditMessageText.builder()
                .chatId(id.toString())
                .messageId(msgId).text("").build();

        EditMessageReplyMarkup newKb = EditMessageReplyMarkup.builder()
                .chatId(id.toString()).messageId(msgId).build();

        if(data.equals(startMenu))
        {
            newTxt.setText("Start menu");
            newKb.setReplyMarkup(getStartMenu());
        }
        if(data.equals(getMyStores)) {
            newTxt.setText("My stores");
            newKb.setReplyMarkup(getMyStores(id));
        }
        if(data.equals(createStore)) {
            UserBot userBot = dataBase.getUser(id);
            owait.put(userBot.getTgId(), createStore);
            newTxt.setText("Введите название магазина: ");
        }
        if(Arrays.asList(data.split(":")).get(0).equals(storeCommand)) {
            List<String> ddaq = Lists.newArrayList(data.split(":"));
            ddaq.remove(0);
            UserBot userBot = dataBase.getUser(id);


            newTxt.setText(storeProcesses(userBot, ddaq));
//            List<InlineKeyboardButton> list = new ArrayList<>();
//            InlineKeyboardMarkup da = new InlineKeyboardMarkup();
//
//            List<List<InlineKeyboardButton>> storeLines = new ArrayList<>();
//            storeLines.add(List.of(InlineKeyboardButton.builder().text("◀ Назад").callbackData(getMyStores).build()));
//
//            newKb.setReplyMarkup(InlineKeyboardMarkup.builder().keyboard(storeLines).build());

        }


        AnswerCallbackQuery close = AnswerCallbackQuery.builder()
                .callbackQueryId(queryId).build();

        try {
            execute(close);
            execute(newTxt);
            if(!data.equals(createStore) && !data.contains(storeEdit))
                execute(newKb);
        }
        catch (TelegramApiException d)
        {
            d.printStackTrace();
        }
    }
    private InlineKeyboardMarkup getMyStores(long id)
    {
        UserBot userBot = dataBase.getUser(id);
        List<Store> stores = userBot.getStores();
        List<List<InlineKeyboardButton>> storeLines = new ArrayList<>();

        for (Store store : stores) {
            List<InlineKeyboardButton> line = new ArrayList<>();
            List<InlineKeyboardButton> lineFunc = new ArrayList<>();
            line.add(
                    InlineKeyboardButton
                            .builder()
                            .text("#"+store.getId() + " " + store.getTitle())
                            .callbackData("store:get:" + store.getId())
                            .build());
            lineFunc.add(InlineKeyboardButton
                    .builder()
                    .text("✏")
                    .callbackData("store:edit:" + store.getId())
                    .build());
            lineFunc.add(InlineKeyboardButton
                    .builder()
                    .text("❌")
                    .callbackData("store:delete:" + store.getId())
                    .build());
            storeLines.add(line);
            storeLines.add(lineFunc);
        }
        storeLines.add(List.of(InlineKeyboardButton.builder().text("◀ Назад").callbackData(startMenu).build()));

        return InlineKeyboardMarkup.builder().keyboard(storeLines)
                .build();
    }
    public String storeProcesses(UserBot userBot, List<String> data)
    {
        if(data.get(0).equals("delete"))
        {
            Store store = Store.getStore(Long.valueOf(data.get(1)));
            userBot.deleteStore(store);
            return "Магазин "+ store.getTitle() + " удален!";
        }
        if(data.get(0).equals("edit"))
        {
            owait.put(userBot.getTgId(),"store:edit:"+data.get(1));
            return "В ведите новое название магазина: ";
        }
        return "Ниче не произошло";
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

    private Store createStore(UserBot userBot, String title)
    {
        return userBot.createStoreUser(title);
    }
}
