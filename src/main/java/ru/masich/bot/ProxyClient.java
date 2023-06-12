package ru.masich.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.StartBotUser;
import ru.masich.bot.Client.ClientAwait;
import ru.masich.bot.Client.ClientButton;
import ru.masich.bot.Client.ClientMessage;
import ru.masich.bot.Client.ClientPreCheckoutQuery;
import ru.masich.bot.DAO.IMPL.AwaitDAOimpl;
import ru.masich.bot.DAO.IMPL.UserBotDAOImpl;
import ru.masich.bot.DAO.interfaces.AwaitDao;
import ru.masich.bot.DAO.interfaces.UserBotDAO;
import ru.masich.bot.entity.Await;
import ru.masich.bot.entity.UserBot;

public class ProxyClient {
    public StartBotUser startBotUser;
    public UserBotDAO userBotDAO = new UserBotDAOImpl();
    public AwaitDao awaitDao = new AwaitDAOimpl();
    public UserBot userBot;
    public Await await;
    public final static int shopID = 29;
    Logger logger = LoggerFactory.getLogger(ProxyClient.class);
    public ProxyClient(StartBotUser startBotUser) {
        this.startBotUser = startBotUser;
    }

    public void proxy() throws TelegramApiException {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< proxy");
        //Предчек
        if(startBotUser.update.hasPreCheckoutQuery()) {
            getFrom(startBotUser.update.getPreCheckoutQuery().getFrom());
            ClientPreCheckoutQuery.execute(this);
            return;
        }

        //Если есть отложенные команды
        if(await != null) {
            ClientAwait.execute(this);
            return;
        }
        //если была нажата конка
        if(startBotUser.update.hasCallbackQuery()) {
            getFrom(startBotUser.update.getCallbackQuery().getFrom());
            ClientButton.execute(this);
            return;
        }
        //Если сообщение
        if(startBotUser.update.hasMessage()) {
            if(startBotUser.update.getMessage().hasSuccessfulPayment())
            {
                ClientMessage.sendMessage("Платеж подтвержден");
                return;
            }

            getFrom(startBotUser.update.getMessage().getFrom());
            ClientMessage.execute(this);
            return;
        }

        System.out.println("Хз че делать с "+startBotUser.update);

    }

    private void getFrom(User user)
    {
        logger.info("("+this.getClass().getSimpleName()+".java:"+new Throwable().getStackTrace()[0].getLineNumber()+")"+"<< getFrom");
        UserBot userBotTh = userBotDAO.getUserBot(user);
        //Проверяем если пользователь в базе если есть проверяем изменились ли у него поля, если поменялись, тогда обновляем егов базе
        if(userBotTh == null)
        {
            userBotDAO.save(new UserBot(user));
        }
        if(userBotTh != null && !userBotTh.equalsUt(user))
        {
            //Берем данные от сервера телеги
            UserBot userBot2 = new UserBot(user);
            //Прописываем id
            userBot2.setId(userBotTh.getId());
            //Сохраняем
            userBotDAO.update(userBot2);
        }
        userBot = userBotTh;
        await = awaitDao.get(userBotTh.getId());
    }
}
