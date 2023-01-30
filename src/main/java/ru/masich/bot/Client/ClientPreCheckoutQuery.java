package ru.masich.bot.Client;

import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.bot.ProxyClient;

public class ClientPreCheckoutQuery {
    public static void execute(ProxyClient proxyClient) {
        PreCheckoutQuery pre = proxyClient.startBotUser.update.getPreCheckoutQuery();
        AnswerPreCheckoutQuery answerPreCheckoutQuery = new AnswerPreCheckoutQuery();
        answerPreCheckoutQuery.setPreCheckoutQueryId(pre.getId());
        answerPreCheckoutQuery.setOk(true);
        try {
            System.out.println();
            System.out.println(proxyClient.startBotUser.execute(answerPreCheckoutQuery));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
