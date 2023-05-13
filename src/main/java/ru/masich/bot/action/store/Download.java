package ru.masich.bot.action.store;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
import ru.masich.bot.entity.ProductSize;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;


public class Download {
    private static StoreDao storeDao = new StoreDAOimpl();
    private static CatalogDAO catalogDAO = new CatalogDAOimpl();
    private static ProductDAO productDAO = new ProductDAOimpl();
    private Button butIn;
    int shopid;


    public Download(Button button, int shopid)
    {
        this.butIn = button;
        this.shopid= shopid;
    }
    public void check()
    {

        StringBuilder updates = new StringBuilder();

        updates.append(catalogCheck("Категории!A3:F"));
        updates.append(productCheck("Товары!A3:F"));
        updates.append(sizeCheck("Размер!A3:F"));


//        List<List<Object>> catalog;
//        List<List<Object>> product;
//        List<List<Object>> size;
//        List<List<Object>> count;
//        try {
//            catalog = Sheets.get("Категории!A3:F");
//            product = Sheets.get("Товары!A3:F");
//            size = Sheets.get("Размер!A3:F");
//            count = Sheets.get("Количество!A3:F");
//        } catch (GeneralSecurityException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        List<Catalog> ggTable = convertCatalog(catalog);
//        List<Catalog> db = catalogDAO.getCatalogAllStore(Long.valueOf(shopid));
//
//        List<Catalog> catDelete = db.stream().filter(x -> {
//            List<Catalog> dqe = ggTable.stream().filter(p ->
//                    p.getId() != null && p.getId().equals(x.getId())).toList();
//            if(dqe.size() > 0)
//                return false;
//            else {
//                updates.append("Catalog: ");
//                updates.append(x.getId());
//                updates.append(" будет удаленн! \uD83D\uDDD1 ");
//                updates.append("\r\n");
//                return true;
//            }
//        }).toList();
//
//        for (Catalog cat : ggTable) {
//            if (cat.getId() == null ) {
//                //Создаем новый каталог
//                updates.append("Будет созданн новый каталог: " + cat.toString());
//            } else
//            {
//                Catalog catDB = catalogDAO.get(cat.getId());
//                //=============
//                String upd = catDB.check(cat).toString();
//                if(!upd.equals("")) {
//                    updates.append(upd);
//                    updates.append("\r\n");
//                }
//            }
//        }
//
//        if(updates.length() < 5)
//        {
//            updates.append("Текущий список актуален");
//        }
//
//        try {
//            butIn.startBot.execute(SendMessage.builder()
//                    .chatId(butIn.update.getCallbackQuery().getFrom().getId())
//                    .text(updates.toString())
//                    .build());
//            butIn.startBot.execute(AnswerCallbackQuery.builder()
//                    .callbackQueryId(butIn.update.getCallbackQuery().getId())
//                 //   .text(updates.toString())
//                    .build());
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
    }

    public StringBuilder catalogCheck(String catTable)
    {
        StringBuilder updates = new StringBuilder();

        List<List<Object>> catalog;

        try {
            catalog = Sheets.get(catTable);//Категории!A3:F
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Catalog> ggTable = convertCatalog(catalog);
        List<Catalog> db = catalogDAO.getCatalogAllStore(Long.valueOf(shopid));

        List<Catalog> catDelete = db.stream().filter(x -> {
            List<Catalog> dqe = ggTable.stream().filter(p ->
                    p.getId() != null && p.getId().equals(x.getId())).toList();
            if(dqe.size() > 0)
                return false;
            else {
                updates.append("Catalog: ");
                updates.append(x.getId());
                updates.append(" будет удаленн! \uD83D\uDDD1 ");
                updates.append("\r\n");
                return true;
            }
        }).toList();

        for (Catalog cat : ggTable) {
            if (cat.getId() == null ) {
                //Создаем новый каталог
                updates.append("Будет созданн новый каталог: " + cat.toString());
            } else
            {
                Catalog catDB = catalogDAO.get(cat.getId());
                //=============
                String upd = catDB.check(cat).toString();
                if(!upd.equals("")) {
                    updates.append(upd);
                    updates.append("\r\n");
                }
            }
        }

        if(updates.length() < 5)
        {
            updates.append("Текущий список каталога актуален.\r\n");
        }

        try {
            butIn.startBot.execute(SendMessage.builder()
                    .chatId(butIn.update.getCallbackQuery().getFrom().getId())
                    .text(updates.toString())
                    .build());
            butIn.startBot.execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(butIn.update.getCallbackQuery().getId())
                    //   .text(updates.toString())
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        return updates;
    }
    public StringBuilder productCheck(String proTable)
    {
        StringBuilder updates = new StringBuilder();

        List<List<Object>> products;

        try {
            products = Sheets.get(proTable);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Product> ggTable = convertProduct(products);
        List<Product> db = productDAO.getStore(Long.valueOf(shopid));

        List<Product> proDelete = db.stream().filter(x -> {
            List<Product> dqe = ggTable.stream().filter(p ->
                    p.getId() > 0 && p.getId() == x.getId()).toList();
            if(dqe.size() > 0)
                return false;
            else {
                updates.append("Product: ");
                updates.append(x.getId());
                updates.append(" будет удаленн! \uD83D\uDDD1 ");
                updates.append("\r\n");
                return true;
            }
        }).toList();

        for (Product pro : ggTable) {
            if (pro.getId() == 0) {
                //Создаем новый каталог
                updates.append("Будет созданн новый товар:\r\n" + pro);
            } else
            {
                Catalog catDB = catalogDAO.get(pro.getId());
                //=============
                String upd = pro.check(pro).toString();
                if(!upd.equals("")) {
                    updates.append(upd);
                    updates.append("\r\n");
                }
            }
        }

        if(updates.length() < 5)
        {
            updates.append("Текущий список товаров актуален.\r\n");
        }

        try {
            butIn.startBot.execute(SendMessage.builder()
                    .chatId(butIn.update.getCallbackQuery().getFrom().getId())
                    .text(updates.toString())
                    .build());
            butIn.startBot.execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(butIn.update.getCallbackQuery().getId())
                    //   .text(updates.toString())
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        return updates;
    }
    public StringBuilder sizeCheck(String sizeTable)
    {
        StringBuilder updates = new StringBuilder();
        List<List<Object>> size;

        try {
            //Получаем размеры из таблици
            size = Sheets.get(sizeTable);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        //Конвертируем размеры из таблици в обьекты
        List<ProductSize> ggTable = convertSize(size);
        //Буферный размер, для сокращения числа обращений к бд
        Product buffPS = null;
        //Проходимся по всем размерам
        for(ProductSize ps: ggTable)
        {
            //Если буфер пустой или id отличется от проверяемого
            if(buffPS == null || ps.getProductId() != buffPS.getId())
                buffPS = productDAO.get(ps.getProductId());
            //Получаем обьект размера продукта
            ProductSize psProduct = buffPS.getPtoductSize(ps.getNumber());
            if(!ps.equals(psProduct)) {
                updates.append("Свойство размера продукта: '");
                updates.append(buffPS.getProductAttributes().get("title"));
                updates.append("' -> '");
                updates.append(psProduct.getTitle());
                updates.append("' будет измененно.");
                updates.append("\r\n");
            }
        }

        if(updates.length() < 5)
        {
            updates.append("Текущий список свойств размера актуален.\r\n");
        }

        try {
            butIn.startBot.execute(SendMessage.builder()
                    .chatId(butIn.update.getCallbackQuery().getFrom().getId())
                    .text(updates.toString())
                    .build());
            butIn.startBot.execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(butIn.update.getCallbackQuery().getId())
                    //   .text(updates.toString())
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        return updates;
    }
    public StringBuilder countCheck(String sizeTable)
    {
        StringBuilder updates = new StringBuilder();
        List<List<Object>> count;

        try {
            //Получаем обьект количества из таблици
            count = Sheets.get(sizeTable);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        //Конвертируем размеры из таблици в обьекты
        List<ProductSize> ggTable = convertSize(count);
        //Буферный размер, для сокращения числа обращений к бд
        Product buffPS = null;
        //Проходимся по всем размерам
        for(ProductSize ps: ggTable)
        {
            //Если буфер пустой или id отличется от проверяемого
            if(buffPS == null || ps.getProductId() != buffPS.getId())
                buffPS = productDAO.get(ps.getProductId());
            //Получаем обьект размера продукта
            ProductSize psProduct = buffPS.getPtoductSize(ps.getNumber());
            if(!ps.equals(psProduct)) {
                updates.append("Свойство размера продукта: '");
                updates.append(buffPS.getProductAttributes().get("title"));
                updates.append("' -> '");
                updates.append(psProduct.getTitle());
                updates.append("' будет измененно.");
                updates.append("\r\n");
            }
        }

        if(updates.length() < 5)
        {
            updates.append("Текущий список свойств размера актуален.\r\n");
        }

        try {
            butIn.startBot.execute(SendMessage.builder()
                    .chatId(butIn.update.getCallbackQuery().getFrom().getId())
                    .text(updates.toString())
                    .build());
            butIn.startBot.execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(butIn.update.getCallbackQuery().getId())
                    //   .text(updates.toString())
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        return updates;
    }
    public void execute(){
        StringBuilder updates = new StringBuilder();

        List<List<Object>> catalog;
        List<List<Object>> product;
        List<List<Object>> size;
        List<List<Object>> count;
        try {
            catalog = Sheets.get("Категории!A3:F");
            product = Sheets.get("Товары!A3:F");
            size = Sheets.get("Размер!A3:F");
            count = Sheets.get("Количество!A3:F");
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Изменяем каталоги
        updates.append(catalogExecute(convertCatalog(catalog), catalogDAO.getCatalogAllStore(Long.valueOf(shopid))));
        updates.append(productExecute(convertProduct(product), productDAO.getStore(Long.valueOf(shopid))));

        if(updates.length() < 5)
        {
            updates.append("Текущий список актуален");
        }

        try {
            butIn.startBot.execute(SendMessage.builder()
                    .chatId(butIn.update.getCallbackQuery().getFrom().getId())
                    .text(updates.toString())
                    .build());
            butIn.startBot.execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(butIn.update.getCallbackQuery().getId())
                    //.text(updates.toString())
                    .build());

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    private StringBuilder catalogExecute(List<Catalog> table, List<Catalog> db)
    {
        StringBuilder update = new StringBuilder();

        List<Catalog> catDelete = db.stream().filter(x -> {
            List<Catalog> dqe = table.stream().filter(p -> p.getId() != null && p.getId().equals(x.getId())).toList();
            if(dqe.size() > 0)
                return false;
            else {
                update.append("Catalog: ");
                update.append(x.getId());
                update.append(" будет удаленн!");
                update.append("\r\n");
                catalogDAO.delete(x);
                return true;
            }
        }).toList();

        for (Catalog cat : table) {
            if (cat.getId() == null) {
                //Создаем новый каталог
                update.append("Будет созданн новый каталог: " + cat);
                catalogDAO.set(cat);
            } else
            {
                Catalog catDB = catalogDAO.get(cat.getId());
                //=============
                String upd = catDB.check(cat).toString();
                if(!upd.equals("")) {
                    catalogDAO.update(cat);
                    update.append(upd);
                    update.append("\r\n");
                }
            }
        }
        return update;
    }
    private StringBuilder productExecute(List<Product> table, List<Product> db)
    {
        StringBuilder update = new StringBuilder();

        List<Product> prodDelete = db.stream().filter(x -> {
            List<Product> dqe = table.stream().filter(p -> p.getId() > 0 && p.getId() == x.getId()).toList();
            if(dqe.size() > 0)
                return false;
            else {
                update.append("Товар: ");
                update.append(x.getId());
                update.append(" будет удаленн!");
                update.append("\r\n");
                productDAO.delete(x.getId());
                return true;
            }
        }).toList();

        for (Product prod : table) {
            if (prod.getId() > 0) {
                //Создаем новый каталог
                update.append("Будет созданн новый товар: " + prod);
                productDAO.set(prod);
            } else
            {
                Product prodDb = productDAO.get(prod.getId());
                //=============
                String upd = prodDb.check(prod).toString();
                if(!upd.equals("")) {
                    productDAO.set(prod);
                    update.append(upd);
                    update.append("\r\n");
                }
            }
        }
        return update;
    }
    private List<Catalog> convertCatalog(List<List<Object>> cat)
    {
        List<Catalog> cats = new ArrayList<>();
        for (List<Object> catE : cat)
        {
            Catalog catalog = new Catalog();
            if(!catE.get(0).toString().equals(""))
                catalog.setId(Long.valueOf(catE.get(0).toString()));
            if(!catE.get(1).toString().equals(""))
                catalog.setFatherId(Long.valueOf(catE.get(1).toString()));
            if(!catE.get(2).toString().equals(""))
                catalog.setTitle((String) catE.get(2));
            if(!catE.get(3).toString().equals(""))
                catalog.setShopId(Long.valueOf(catE.get(3).toString()));
            if(!catE.get(4).toString().equals(""))
                catalog.setLevel(Long.valueOf(catE.get(4).toString()));
            if(!catE.get(5).toString().equals(""))
                catalog.setCatalog_atributes(Map.of("photo",catE.get(5)));
            cats.add(catalog);
        }
        return cats;
    }
    private List<Product> convertProduct(List<List<Object>> products)
    {
        List<Product> prodOut = new ArrayList<>();
        for (List<Object> catE : products)
        {
            Product prAr = new Product();
            Map<String, Object> params  = new IdentityHashMap<>();

            //Устанавливаем id продукта
            if(!catE.get(0).toString().equals(""))
                prAr.setId(Integer.parseInt(catE.get(0).toString()));
            //Устанавливаем id магазина
            if(!catE.get(1).toString().equals(""))
                prAr.setShopId(Integer.parseInt(catE.get(1).toString()));
            //Устанавливаем id каталога
            if(!catE.get(2).toString().equals(""))
                prAr.setCatalogId(Integer.parseInt(catE.get(2).toString()));
            //Устанавливаем загаловок
            if(!catE.get(3).toString().equals(""))
                params.put("title", catE.get(3).toString());
            //Устанавсиваем ссылку на фото
            if(!catE.get(4).toString().equals(""))
                params.put("main_photo",catE.get(4).toString());
            //Устанавливаем еденику измерения
            if(!catE.get(5).toString().equals(""))
                params.put("measurement",catE.get(5).toString());

            prAr.setProductAttributes(params);
            prodOut.add(prAr);
        }
        return prodOut;
    }

    private List<ProductSize> convertSize(List<List<Object>> size)
    {
        List<ProductSize> sizeOut = new ArrayList<>();
        for (List<Object> sz : size)
        {
            ProductSize szAr = new ProductSize();
            Map<String, Object> params  = new IdentityHashMap<>();

            //Устанавливаем id товара
            if(!sz.get(0).toString().equals(""))
                szAr.setProductId(Integer.parseInt(sz.get(0).toString()));
            //Устанавливаем уникальный для товара id размера
            if(!sz.get(1).toString().equals(""))
                szAr.setNumber(Integer.parseInt(sz.get(1).toString()));
            //Устанавливаем тип действия
            if(!sz.get(2).toString().equals(""))
                szAr.setAction(sz.get(2).toString());
            //Устанавзиваем использование размера по умолчанию
            if(!sz.get(3).toString().equals(""))
                szAr.setDefaultSize(sz.get(3).toString().equals("1")?true:false);
            //Устанавливаем заголовок размера
            if(!sz.get(4).toString().equals(""))
                szAr.setTitle(sz.get(4).toString());

            sizeOut.add(szAr);
        }
        return sizeOut;
    }
    private StringBuilder checkCatalog(Catalog catDB, Catalog catTable)//✅
    {
        StringBuilder updates = new StringBuilder();
        //Проверяем изменение родителя
        if(!Objects.equals(catDB.getFatherId(), catTable.getFatherId()))
        {
            updates.append("CatalogID: ")
                    .append(catDB.getId())
                    .append(" будет изменен родитель \n\tс ")
                    .append(catDB.getFatherId())
                    .append("\n\tна ")
                    .append(catTable.getFatherId())
                    .append("\n");
        }
        //Проверяем изменение заголовка
        if(!Objects.equals(catDB.getTitle(), catTable.getTitle()))
        {
            updates.append("CatalogID: ")
                    .append(catDB.getId())
                    .append(" будет изменен заголовок \n\tс ")
                    .append(catDB.getTitle())
                    .append(" \n\tна ")
                    .append(catTable.getTitle())
                    .append("\n");
        }
        //Проверяем изменение номер магазина
        if(!Objects.equals(catDB.getShopId(), catTable.getShopId()))
        {
            updates.append("CatalogID: ")
                    .append(catDB.getId())
                    .append(" будет изменен номер магазина\n\tс ")
                    .append(catDB.getShopId())
                    .append("\n\tна ")
                    .append(catTable.getShopId())
                    .append("\n");
        }
        //Проверяем изменение уровня вложенности
        if(!Objects.equals(catDB.getLevel(), catTable.getLevel()))
        {
            updates.append("CatalogID: ")
                    .append(catDB.getId())
                    .append(" будет изменен уровень вложенностиn\tс ")
                    .append(catDB.getLevel())
                    .append("\n\tна ")
                    .append(catTable.getLevel())
                    .append("\n");
        }
        //Проверяем изменение картинки
        if(!Objects.equals(catDB.getCatalog_atributes().get("photo"), catTable.getCatalog_atributes().get("photo")))
        {
            updates.append("CatalogID: ")
                    .append(catDB.getId())
                    .append(" будет изменена картинка\n\tс ")
                    .append(catDB.getCatalog_atributes().get("photo"))
                    .append("\n\tна ")
                    .append(catTable.getCatalog_atributes().get("photo"))
                    .append("\n");
        }
        return updates;
    }
}
