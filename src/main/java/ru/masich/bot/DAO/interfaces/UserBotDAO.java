package ru.masich.bot.DAO.interfaces;

import ru.masich.bot.entity.UserBot;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

public interface UserBotDAO {
    public List<UserBot> getAllUserBot();
    public UserBot getUserBot(Long id);
    public UserBot getUserBot(User user);
    public UserBot update(UserBot userBot);
    public UserBot save(UserBot userBot);
    public UserBot deleteUserBot(UserBot userBot);
}
