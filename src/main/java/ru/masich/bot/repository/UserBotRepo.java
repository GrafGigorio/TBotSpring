package ru.masich.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.masich.bot.entity.UserBot;
@Repository
public interface UserBotRepo extends JpaRepository<UserBot,Long> {
}
