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

    public Upload(Button button, int shopid) {
        this.butIn = button;
        this.shopid = shopid;
    }

    public void execute() {
        List<Catalog> catalogs = catalogDAO.getCatalogAllStore((long) shopid);
        List<Product> products = productDAO.getStore((long) shopid);

        List<List<Object>> catalogOut = new ArrayList<>();
        List<List<Object>> productOut = new ArrayList<>();
        List<List<Object>> productSizeOut = new ArrayList<>();
        List<List<Object>> productCountOut = new ArrayList<>();
        //Преобразуем каталоги в списки для выгрузки в таблцы
        for (Catalog cat : catalogs) {
            Map<String, Object> atr = cat.getCatalog_atributes();
            catalogOut.add(List.of(cat.getId(), cat.getFatherId(), cat.getTitle(), cat.getShopId(), cat.getLevel(), atr.get("photo")));
        }
        //Преобразуем объекты в списки для выгрузки в таблцы
        for (Product prod : products) {

            Map<String, Object> atr = prod.getProductAttributes();
            productOut.add(List.of(prod.getId(), prod.getShopId(), prod.getCatalogId(), atr.get("title"), atr.get("main_photo"), atr.get("measurement")));

            Map<String, Map<String, Object>> size = (Map<String, Map<String, Object>>) atr.get("check_box_prop");
            Map<String, Map<String, Object>> count = (Map<String, Map<String, Object>>) atr.get("count_property");
            //Проходимся по свойствам размера
            if (size != null && size.size() > 0) {
                for (Map.Entry<String, Map<String, Object>> eed : size.entrySet()) {
                    Map<String, Object> sd = eed.getValue();
                    productSizeOut.add(
                            List.of(
                                    String.valueOf(prod.getId()),
                                    String.valueOf(eed.getKey()),
                                    String.valueOf(sd.get("act")),
                                    sd.get("sel") != null ? true:"",
                                    String.valueOf(sd.get("tit"))
                            )
                    );
                }
            }
            //Проходимся по свойствам количества
            if (count != null && count.size() > 0) {
                for (Map.Entry<String, Map<String, Object>> eed : count.entrySet()) {
                    Map<String, Object> sd = eed.getValue();
                    productCountOut.add(
                            List.of(
                                    String.valueOf(prod.getId()),
                                    String.valueOf(eed.getKey()),
                                    String.valueOf(sd.get("act")),
                                    String.valueOf(sd.get("cou")),
                                    String.valueOf(sd.get("tit"))
                            )
                    );
                }
            }
        }

        try {
            Sheets.clear("Категории!A3:F");
            Sheets.save(catalogOut, "Категории!A3:F");

            Sheets.clear("Товары!A3:F");
            Sheets.save(productOut, "Товары!A3:F");

            Sheets.clear("Размер!A3:E");
            Sheets.save(productSizeOut, "Размер!A3:E");

            Sheets.clear("Количество!A3:E");
            Sheets.save(productCountOut, "Количество!A3:E");

            butIn.startBot.execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(butIn.update.getCallbackQuery().getId())
                    .text("Данные сохранены в таблицу")
                    .build());
        } catch (GeneralSecurityException | IOException | TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
