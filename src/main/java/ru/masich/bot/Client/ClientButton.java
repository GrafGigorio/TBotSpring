package ru.masich.bot.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.bot.Client.ObjectActions.ActionProxy;
import ru.masich.bot.DAO.IMPL.CatalogDAOimpl;
import ru.masich.bot.DAO.IMPL.ProductDAOimpl;
import ru.masich.bot.DAO.interfaces.CatalogDAO;
import ru.masich.bot.DAO.interfaces.ProductDAO;
import ru.masich.bot.ProxyClient;
import ru.masich.bot.Var;
import ru.masich.bot.entity.Catalog;
import ru.masich.bot.entity.Product;
import ru.masich.bot.menu.MenuClient;

import java.util.List;

public class ClientButton {
    public static ProxyClient proxyClient;
    static Logger logger = LoggerFactory.getLogger(ClientButton.class);
    public static void execute(ProxyClient proxyClient) throws TelegramApiException {

        ClientButton.proxyClient = proxyClient;
        CallbackQuery callbackQuery = proxyClient.startBotUser.update.getCallbackQuery();
        logger.info("<< execute " + callbackQuery.getData());

        String  sd = callbackQuery.getData();
        //если префикс обьекта objectId
        if(callbackQuery.getData().contains("objId"))
        {
            logger.info("<< execute objId ");
            ActionProxy actionProxy = new ActionProxy(proxyClient);
            actionProxy.proxy();
        }
        //если редактирование корзины
        if(callbackQuery.getData().contains("chartEdit"))
        {
            logger.info("<< execute chartEdit");
            ActionProxy actionProxy = new ActionProxy(proxyClient);
            actionProxy.proxy();
        }
        //Если нажата кнопка получения каталога
        if(callbackQuery.getData().contains(Var.catalogGet))
        {
            logger.info("<< execute chartEdit " + Var.catalogGet);
            String[] arr = callbackQuery.getData().split(":");
            CatalogDAO catalogDAO = new CatalogDAOimpl();
            ProductDAO productDAO = new ProductDAOimpl();
            //Получаем дочерние каталоги
            List<Catalog> catalogs = catalogDAO.getChildren(Long.valueOf(arr[2]));
            //Получаем продукты
            List<Product> products = productDAO.getCatalog(Long.valueOf(arr[2]));

            //Обрабатываем каталоги
            if (catalogs.size() > 0)
                MenuClient.sendCatalogs(callbackQuery.getFrom().getId(),catalogs);
            //Обрабатываем продукты
            if(products.size() > 0)
                MenuClient.sendProducts(callbackQuery.getFrom().getId(), products);

            proxyClient.startBotUser.execute(
                    AnswerCallbackQuery.builder()
                            .callbackQueryId(proxyClient.startBotUser.update.getCallbackQuery().getId())
                            .build()
            );

            //Если каталог пуст отправляем гуся
            if(catalogs.size() == 0 && products.size() == 0)
            {
                try {
                    SendSticker sticker = new SendSticker();
                    sticker.setChatId(callbackQuery.getFrom().getId());
                    //Гусь)
                    sticker.setSticker(new InputFile("CAACAgIAAxkBAAIBEWPYCpwUsucMYdYksaPxgBPGaUcFAAIMAAO4PGQIAAKrIbu0foMtBA"));

                    proxyClient.startBotUser.execute(sticker);
                    proxyClient.startBotUser.execute(SendMessage.builder().chatId(callbackQuery.getFrom().getId()).text("Каталог " + catalogDAO.get(arr[2]).getTitle() + " Пуст :((").build());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
