package ru.masich.bot.action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.masich.StartBot;
import ru.masich.bot.action.Awaits.AwaitTable;

import java.util.List;

public class AdminBigObjects {
    static Logger logger = LogManager.getLogger(AwaitTable.class);
    List<String> commandSeq;
    private StartBot startBot;
    private Update update;
    private String callbackId;
    long userBotID;

    public AdminBigObjects(List<String> commandSeq, StartBot startBot, Update update, String callbackId, Long userBotID) {
        this.startBot = startBot;
        this.update = update;
        this.callbackId = callbackId;
        this.userBotID = userBotID;
        this.commandSeq = commandSeq;
    }

    public void exec() {

    }
}
