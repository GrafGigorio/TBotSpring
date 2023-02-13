package ru.masich.webApp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.masich.bot.DAO.interfaces.UserBotDAO;
import ru.masich.bot.DAO.IMPL.UserBotDAOImpl;
import ru.masich.bot.entity.UserBot;

@Controller
public class WebAppController {



    @RequestMapping("/")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {

        UserBotDAO userBotDAO = new UserBotDAOImpl();
        UserBot userBot = userBotDAO.getUserBot(348L);



        System.out.println("userBot: "+userBot);

        model.addAttribute("name", userBot.getFirstName());
        return "test";
    }
}
