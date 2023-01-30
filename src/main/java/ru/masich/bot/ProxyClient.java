package ru.masich.bot;

import org.telegram.telegrambots.meta.api.objects.User;
import ru.masich.StartBotUser;
import ru.masich.bot.Client.ClientAwait;
import ru.masich.bot.Client.ClientButtun;
import ru.masich.bot.Client.ClientMessage;
import ru.masich.bot.Client.ClientPreCheckoutQuery;
import ru.masich.bot.DAO.AwaitDAOimpl;
import ru.masich.bot.DAO.AwaitDao;
import ru.masich.bot.DAO.UserBotDAO;
import ru.masich.bot.DAO.UserBotDAOImpl;
import ru.masich.bot.entity.Await;
import ru.masich.bot.entity.UserBot;

public class ProxyClient {
    public StartBotUser startBotUser;
    public UserBotDAO userBotDAO = new UserBotDAOImpl();
    public AwaitDao awaitDao = new AwaitDAOimpl();
    public UserBot userBot;
    public Await await;
    public final static int shopID = 29;
    public ProxyClient(StartBotUser startBotUser) {
        this.startBotUser = startBotUser;
    }

    public void proxy()
    {
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
            ClientButtun.execute(this);
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
