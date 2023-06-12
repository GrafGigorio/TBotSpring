package ru.masich.bot.Client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.invoices.CreateInvoiceLink;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChat;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.bot.ProxyClient;
import ru.masich.bot.menu.ChartMenu;
import ru.masich.bot.menu.MenuClient;

import java.util.ArrayList;
import java.util.List;

public class ClientMessage {
    public static ProxyClient proxyClient;
    static Logger logger = LogManager.getLogger(ClientMessage.class);
    public static void execute(ProxyClient proxyClient) throws TelegramApiException {

        ClientMessage.proxyClient = proxyClient;
        String mes = proxyClient.startBotUser.update.getMessage().getText();
        String retMes="123";

        logger.info("(ClientMessage"+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< execute mes " + mes);
        if(mes == null)
        {
            System.out.println("=============");
            System.out.println(proxyClient.startBotUser.update.getMessage().getSticker().getCustomEmojiId());
            System.out.println(proxyClient.startBotUser.update.getMessage().getSticker().getEmoji());
            System.out.println(proxyClient.startBotUser.update.getMessage().getSticker().getFileId());
            System.out.println("=============");
        }

        if(mes.equals("/start"))
        {
            retMes = "Привет "+proxyClient.userBot.getFirstName()+", вот наш каталог";
            MenuClient.sendStartMenu(proxyClient.userBot, retMes, ProxyClient.shopID);
        }

        if(mes.equals("/chart"))
        {
            retMes = proxyClient.userBot.getFirstName()+", вот ваша корзина:";
            ChartMenu chartMenu = new ChartMenu();
            chartMenu.sendActiveChartNew(proxyClient.startBotUser, retMes);
        }
    }
    static public Message sendMessage(String mes)
    {
        logger.info("(ClientMessage"+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<<  sendMessage");
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("start", "Начало"));
        commands.add(new BotCommand("catalog", "Каталог"));
        commands.add(new BotCommand("chart", "Корзина"));

        LabeledPrice labeledPrice = new LabeledPrice();
        labeledPrice.setLabel("Товар1");
        labeledPrice.setAmount(10000);
        LabeledPrice labeledPrice2 = new LabeledPrice();
        labeledPrice2.setLabel("Товар2");
        labeledPrice2.setAmount(10001);
        LabeledPrice labeledPrice3 = new LabeledPrice();
        labeledPrice3.setLabel("Доставка");
        labeledPrice3.setAmount(1002);
        List<LabeledPrice> prices = new ArrayList<>();
        prices.add(labeledPrice);
        prices.add(labeledPrice2);
        prices.add(labeledPrice3);

        CreateInvoiceLink invoiceLink = CreateInvoiceLink.builder()
                .title("123")
                .description("asdeda")
                .payload("31")
                .providerToken("381764678:TEST:49302")
                .currency("RUB")
                .prices(prices)
                .needShippingAddress(true)
                .build();


        SendInvoice sendInvoice = SendInvoice.builder().chatId(
                ClientMessage.proxyClient.userBot.getTgId()).description("asd")
                .payload("234")
                .maxTipAmount(1000)
                .title("asdwad")
                .providerToken("381764678:TEST:49302")
                .startParameter("123")
                .currency("RUB")
                .needShippingAddress(true)
                .prices(prices)
                .build();
        SendMessage smd = SendMessage.builder().chatId(ClientMessage.proxyClient.userBot.getTgId())
                .text(mes).build();


//        SendMediaGroup sendMediaGroup = SendMediaGroup.builder().chatId(ClientMessage.proxyClient.userBot.getTgId()).medias(
//                List.of(
//                        InputMediaPhoto.builder().media("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSKMYVWRbHe6k3h3m3B-fVktFOHDV0ZnLFDiw&usqp=CAU").build(),
//                        InputMediaPhoto.builder().media("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRKmZ8cwXpir4dl5y97ysCHSr_hkq6LG94F1g&usqp=CAU").build())
//        ).build();

//        SendPhoto sendPhoto = SendPhoto.builder()
//                .chatId(ClientMessage.proxyClient.userBot.getTgId())
//                .photo(new InputFile("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQxjiaZpCUUHhjsk8OG6LxxR1uVrIgInQXV2w&usqp=CAU"))
//                .replyMarkup(
//                        InlineKeyboardMarkup.builder().keyboardRow(List.of(
//                                InlineKeyboardButton.builder()
//                                        .text("100г").callbackData(Var.createStore)
//                                        .build(),
//                                InlineKeyboardButton.builder()
//                                        .text("200г").callbackData(Var.createStore)
//                                        .build()
//                        )
//                    ).build()
//                )
//                .build();
        try {
            System.out.println( "LINL ->>" + ClientMessage.proxyClient.startBotUser.execute(invoiceLink));
            BotCommandScopeChat botCommandScopeChat = new BotCommandScopeChat();
            botCommandScopeChat.setChatId(proxyClient.userBot.getTgId());
            ClientMessage.proxyClient.startBotUser.execute(sendInvoice);
            ClientMessage.proxyClient.startBotUser.execute(new SetMyCommands(commands,botCommandScopeChat,null));

//             ClientMessage.proxyClient.startBotUser.execute(sendMediaGroup);
//             ClientMessage.proxyClient.startBotUser.execute(sendPhoto);
            return ClientMessage.proxyClient.startBotUser.execute(smd);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static Message sendMenu(Long who, String txt, InlineKeyboardMarkup kb) {
        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("HTML").text(txt)
                .replyMarkup(kb).build();
        try {
            return proxyClient.startBotUser.execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
