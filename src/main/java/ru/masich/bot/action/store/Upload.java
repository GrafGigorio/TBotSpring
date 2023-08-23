package ru.masich.bot.action.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.Sheets.GoogleSheets;
import ru.masich.StartBot;
import ru.masich.bot.DAO.IMPL.CatalogDAOimpl;
import ru.masich.bot.DAO.IMPL.ProductDAOimpl;
import ru.masich.bot.DAO.IMPL.StoreDAOimpl;
import ru.masich.bot.DAO.interfaces.CatalogDAO;
import ru.masich.bot.DAO.interfaces.ProductDAO;
import ru.masich.bot.DAO.interfaces.StoreDao;
import ru.masich.bot.entity.Catalog;
import ru.masich.bot.entity.Product;
import ru.masich.bot.entity.Store;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Upload {
    static Logger logger = LoggerFactory.getLogger(Upload.class);
    private static StoreDao storeDao = new StoreDAOimpl();
    private static CatalogDAO catalogDAO = new CatalogDAOimpl();
    private static ProductDAO productDAO = new ProductDAOimpl();
    private StartBot startBot;
    private Store store;
    private String callbackID;

    public Upload(Store store, StartBot startBot, String callbackID) {
        this.startBot = startBot;
        this.store = store;
        this.callbackID = callbackID;
    }

    public void execute() {
        logger.info("(" + this.getClass().getSimpleName() + ".java:"
                + new Throwable().getStackTrace()[0].getLineNumber() + ")" + "<< execute ");

        List<Catalog> catalogs = catalogDAO.getCatalogAllStore(store.getId());
        List<Product> products = productDAO.getStore(store.getId());

        List<List<Object>> catalogOut = new ArrayList<>();
        List<List<Object>> productOut = new ArrayList<>();
        List<List<Object>> productSizeOut = new ArrayList<>();
        List<List<Object>> productCountOut = new ArrayList<>();
        //Преобразуем каталоги в списки для выгрузки в таблцы
        for (Catalog cat : catalogs) {
            Map<String, Object> atr = cat.getCatalog_atributes();
            if (atr != null)
                catalogOut.add(List.of(cat.getId(), cat.getFatherId(), cat.getTitle(), cat.getShopId(), cat.getLevel(), atr.get("photo")));
            else
                catalogOut.add(List.of(cat.getId(), cat.getFatherId(), cat.getTitle(), cat.getShopId(), cat.getLevel(), ""));
        }


        logger.info("(" + this.getClass().getSimpleName() + ".java:"
                + new Throwable().getStackTrace()[0].getLineNumber() + ")" + "<< products " + products.size());
        //Преобразуем объекты в списки для выгрузки в таблцы
        for (Product prod : products) {
            Map<String, Map<String, Object>> size = new LinkedHashMap<>();
            Map<String, Map<String, Object>> count = new LinkedHashMap<>();

            Map<String, Object> atr = prod.getProductAttributes();
            if (atr == null) {
                atr = new LinkedHashMap<>();
                productOut.add(List.of(prod.getId(), prod.getShopId(), prod.getCatalogId(), "", "", ""));
            } else {
                productOut.add(
                        List.of(prod.getId(),
                                prod.getShopId(),
                                prod.getCatalogId(),
                                atr.get("title"),
                                atr.get("main_photo") == null ? "" : atr.get("main_photo"),
                                atr.get("measurement")== null ? "" : atr.get("measurement")));
                size = (Map<String, Map<String, Object>>) atr.get("check_box_prop");
                count = (Map<String, Map<String, Object>>) atr.get("count_property");
            }


            //Проходимся по свойствам размера
            if (size != null && size.size() > 0) {
                for (Map.Entry<String, Map<String, Object>> eed : size.entrySet()) {
                    Map<String, Object> sd = eed.getValue();
                    productSizeOut.add(
                            List.of(
                                    String.valueOf(prod.getId()),
                                    String.valueOf(eed.getKey()),
                                    String.valueOf(sd.get("act")),
                                    sd.get("sel") != null ? true : "",
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
            logger.info("(" + this.getClass().getSimpleName() + ".java:"
                    + new Throwable().getStackTrace()[0].getLineNumber() + ")" + "<< execute Категории!A3:F");
            GoogleSheets.clear(store.getTableID(), "Категории!A3:F");
            GoogleSheets.save(store.getTableID(), catalogOut, "Категории!A3:F");

            logger.info("(" + this.getClass().getSimpleName() + ".java:"
                    + new Throwable().getStackTrace()[0].getLineNumber() + ")" + "<< execute Товары!A3:F");
            GoogleSheets.clear(store.getTableID(), "Товары!A3:F");
            GoogleSheets.save(store.getTableID(), productOut, "Товары!A3:F");

            logger.info("(" + this.getClass().getSimpleName() + ".java:"
                    + new Throwable().getStackTrace()[0].getLineNumber() + ")" + "<< execute Размер!A3:E");
            GoogleSheets.clear(store.getTableID(), "Размер!A3:E");
            GoogleSheets.save(store.getTableID(), productSizeOut, "Размер!A3:E");

            logger.info("(" + this.getClass().getSimpleName() + ".java:"
                    + new Throwable().getStackTrace()[0].getLineNumber() + ")" + "<< execute Количество!A3:E");
            GoogleSheets.clear(store.getTableID(), "Количество!A3:E");
            GoogleSheets.save(store.getTableID(), productCountOut, "Количество!A3:E");

            startBot.execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackID)
                    .text("Данные сохранены в таблицу")
                    .build());
        } catch (GeneralSecurityException | IOException | TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
