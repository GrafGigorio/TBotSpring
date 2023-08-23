package ru.masich.bot.action.Awaits;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.masich.StartBot;
import ru.masich.bot.action.store.StoreAction;
import ru.masich.bot.entity.Await;
import ru.masich.bot.entity.UserBot;

import java.util.List;

public class AwaitStore {
    static Logger logger = LogManager.getLogger(AwaitStore.class);
    List<String> commandSeq;
    UserBot userBot;
    Update update;
    StartBot startBot;
    Await await;

    public AwaitStore(List<String> commandSeq, UserBot userBot, Update update, StartBot startBot, Await await) {
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
            StoreAction storeAction = new StoreAction(userBot, update, startBot);
            storeAction.create();
        }
        if (commandSeq.get(0).equals("edit")) {
            logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+" edit");
            commandSeq.remove(0);
            //Переименовывание магазина
            StoreAction storeAction = new StoreAction(userBot,update,startBot);
            storeAction.edit(Long.valueOf(commandSeq.get(0)));
        }

    }
}
