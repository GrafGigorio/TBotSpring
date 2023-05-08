package ru.masich.bot.action.store;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.Sheets.Sheets;
import ru.masich.bot.DAO.IMPL.CatalogDAOimpl;
import ru.masich.bot.DAO.IMPL.ProductDAOimpl;
import ru.masich.bot.DAO.IMPL.StoreDAOimpl;
import ru.masich.bot.DAO.interfaces.CatalogDAO;
import ru.masich.bot.DAO.interfaces.ProductDAO;
import ru.masich.bot.DAO.interfaces.StoreDao;
import ru.masich.bot.action.Button;
import ru.masich.bot.entity.Catalog;
import ru.masich.bot.entity.Product;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Upload {
    private static StoreDao storeDao = new StoreDAOimpl();
    private static CatalogDAO catalogDAO = new CatalogDAOimpl();
    private static ProductDAO productDAO = new ProductDAOimpl();
    private Button butIn;
    int shopid;
    public Upload(Button button, int shopid)
    {
        this.butIn = button;
        this.shopid= shopid;
    }

    public void execute()
    {
        List<Catalog> catalogs = catalogDAO.getCatalogAllStore((long) shopid);
        List<Product> products = productDAO.getStore((long) shopid);

        List<List<Object>> catalogOut = new ArrayList<>();
        for (Catalog cat : catalogs)
        {
            Map<String, Object> atr = cat.getCatalog_atributes();
            catalogOut.add(List.of(cat.getId(),cat.getFatherId(),cat.getTitle(),cat.getShopId(),cat.getLevel(),atr.get("photo")));
        }

        List<List<Object>> productOut = new ArrayList<>();
        List<List<Object>> productSizeOut = new ArrayList<>();
        List<List<Object>> productCountOut = new ArrayList<>();
        //Проходимся по продуктам из базы
        for (Product prod : products)
        {

            Map<String, Object> atr = prod.getProductAttributes();
            productOut.add(List.of(prod.getId(),prod.getShopId(),prod.getCatalogId(),atr.get("title"),atr.get("main_photo"),atr.get("measurement")));

            List<Map<String,Object>> size = (List<Map<String, Object>>) atr.get("check_box_prop");
            List<Map<String,Object>> count = (List<Map<String, Object>>) atr.get("count_property");
            if(size != null && size.size() > 0)
            {
                for (Map<String,Object> sd : size)
                {
                    productSizeOut.add(List.of(prod.getId(),sd.get("id"),sd.get("act"), sd.get("sel") == null ? 0 : sd.get("sel"), sd.get("tit")));
                }
            }
            if(size != null && count.size() > 0)
            {
                for (Map<String,Object> sd : count)
                {
                    productCountOut.add(List.of(prod.getId(),sd.get("id"),sd.get("act"), sd.get("cou"), sd.get("tit")));
                }
            }
        }

        try {

            Sheets.clear("Категории!A3:F");
            Sheets.save(catalogOut,"Категории!A3:F");

            Sheets.clear("Товары!A3:F");
            Sheets.save(productOut,"Товары!A3:F");

            Sheets.clear("Размер!A3:E");
            Sheets.save(productSizeOut,"Размер!A3:E");

            Sheets.clear("Количество!A3:E");
            Sheets.save(productCountOut,"Количество!A3:E");

            butIn.startBot.execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(butIn.update.getCallbackQuery().getId())
                    .text("Данные сохранены в таблицу")
                    .build());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
