package ru.masich.bot.action.store;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.masich.Sheets.Sheets;
import ru.masich.bot.DAO.IMPL.CatalogDAOimpl;
import ru.masich.bot.DAO.IMPL.ProductDAOimpl;
import ru.masich.bot.DAO.interfaces.CatalogDAO;
import ru.masich.bot.DAO.interfaces.ProductDAO;
import ru.masich.bot.action.Button;
import ru.masich.bot.entity.Catalog;
import ru.masich.bot.entity.Product;
import ru.masich.bot.entity.ProductCount;
import ru.masich.bot.entity.ProductSize;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

public class Download {
    private static final CatalogDAO catalogDAO = new CatalogDAOimpl();
    private static final ProductDAO productDAO = new ProductDAOimpl();
    private final Button butIn;
    int shopid;


    public Download(Button button, int shopid)
    {
        this.butIn = button;
        this.shopid= shopid;
    }
    public void check()
    {
        catalogCheck("Категории!A3:F");
        productCheck("Товары!A3:F","Размер!A3:F","Количество!A3:F");
        sizeCheck("Размер!A3:F");
        countCheck("Количество!A3:F");
    }

    public void catalogCheck(String catTable) {
        StringBuilder updates = new StringBuilder();

        List<List<Object>> catalog;

        try {
            catalog = Sheets.get(catTable);//Категории!A3:F
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        List<Catalog> ggTable = convertCatalog(catalog);
        List<Catalog> db = catalogDAO.getCatalogAllStore((long) shopid);

        db.stream().filter(x -> {
            List<Catalog> dqe = ggTable.stream().filter(p ->
                    p.getId() != null && p.getId().equals(x.getId())).toList();
            if (dqe.size() > 0)
                return false;
            else {
                updates.append("Catalog: ");
                updates.append(x.getId());
                updates.append(" будет удаленн! \uD83D\uDDD1 ");
                updates.append("\r\n");
                return true;
            }
        });

        for (Catalog cat : ggTable) {
            if (cat.getId() == null ) {
                //Создаем новый каталог
                updates.append("Будет созданн новый каталог: ").append(cat.toString());
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
            updates.append("Текущий список каталога актуален.✅\r\n");
        }

        getStringBuilder(updates);
    }
    public void productCheck(String proTable,String sizeTable,String countTable) {
        StringBuilder updates = new StringBuilder();
        List<List<Object>> products;
        List<List<Object>> size;
        List<List<Object>> count;

        try {
            products = Sheets.get(proTable);
            size = Sheets.get(sizeTable);
            count = Sheets.get(countTable);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        List<Product> ggTable = convertProduct(products);
        List<Product> db = productDAO.getStore((long) shopid);

        db.stream().filter(x -> {
            List<Product> dqe = ggTable.stream().filter(p ->
                    p.getId() > 0 && p.getId() == x.getId()).toList();
            if (dqe.size() > 0)
                return false;
            else {
                updates.append("Product: ");
                updates.append(x.getId());
                updates.append(" будет удаленн! \uD83D\uDDD1 ");
                updates.append("\r\n");
                return true;
            }
        });

        for (Product pro : ggTable) {
            //Добавляем в продукт свойства размера
            pro.setProductSize(convertSize(size));
            //Добавляем в продукт свойства количества
            pro.setProductCount(convertCount(count));
            if (pro.getId() == 0) {
                //Создаем новый каталог
                updates.append("1 Будет созданн новый товар:\r\n").append(pro);
            } else
            {
                Product proDB = productDAO.get(pro.getId());
                //=============
                String upd = proDB.check(pro).toString();
                if(!upd.equals("")) {
                    updates.append(upd);
                    updates.append("\r\n");
                }
            }
        }

        if(updates.length() < 5)
        {
            updates.append("Текущий список товаров актуален.✅\r\n");
        }

        getStringBuilder(updates);
    }
    public void sizeCheck(String sizeTable) {
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
            ProductSize sizeDb = buffPS.getProductSize(ps.getNumber());

            if(sizeDb == null)
            {
                updates.append("Свойство размера продукта: '");
                updates.append(buffPS.getProductAttributes().get("title"));
                updates.append("' -> '");
                updates.append(ps.getTitle());
                updates.append("' будет добавлено.");
                updates.append("\r\n");
            }
            if(sizeDb != null && !ps.equals(sizeDb)) {
                updates.append("Свойство размера продукта: '");
                updates.append(buffPS.getProductAttributes().get("title"));
                updates.append("' -> '");
                updates.append(sizeDb.getTitle());
                updates.append("' будет измененно.");
                updates.append("\r\n");
            }
        }

        if(updates.length() < 5)
        {
            updates.append("Текущий список свойств размера актуален.✅\r\n");
        }

        getStringBuilder(updates);
    }
    public void countCheck(String sizeTable) {
        StringBuilder updates = new StringBuilder();
        List<List<Object>> countTable;

        try {
            //Получаем обьект количества из таблицы
            countTable = Sheets.get(sizeTable);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        //Конвертируем размеры из таблици в обьекты
        List<ProductCount> cTable = convertCount(countTable);
        //Буферный размер, для сокращения числа обращений к бд
        Product buffPS = null;
        //Проходимся по всем количествам
        for(ProductCount ps: cTable)
        {
            //Если буфер пустой или id отличется от проверяемого
            if(buffPS == null || ps.getProductId() != buffPS.getId())
                buffPS = productDAO.get(ps.getProductId());
            //Получаем обьект размера продукта
            ProductCount pcProduct = buffPS.getProductCount(ps.getNumber());
            if(pcProduct == null)
            {
                updates.append("Свойство количества продукта: '");
                updates.append(buffPS.getProductAttributes().get("title"));
                updates.append("' -> '");
                updates.append(ps.getTitle());
                updates.append("' будет добавлено.");
                updates.append("\r\n");
            }
            if(pcProduct != null && !ps.equals(pcProduct)) {
                updates.append("Свойство количества продукта: '");
                updates.append(buffPS.getProductAttributes().get("title"));
                updates.append("' -> '");
                updates.append(pcProduct.getTitle());
                updates.append("' будет измененно.");
                updates.append("\r\n");
            }
        }

        if(updates.length() < 5)
        {
            updates.append("Текущий список свойств количества актуален.✅\r\n");
        }

        getStringBuilder(updates);
    }
    private void getStringBuilder(StringBuilder updates) {
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
    }
    private List<Catalog> convertCatalog(List<List<Object>> cat) {
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
    private List<Product> convertProduct(List<List<Object>> products) {
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
    private List<ProductSize> convertSize(List<List<Object>> size) {
        List<ProductSize> sizeOut = new ArrayList<>();
        for (List<Object> sz : size)
        {
            ProductSize szAr = new ProductSize();

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
                szAr.setDefaultSize(sz.get(3).toString().equals("TRUE"));
            //Устанавливаем заголовок размера
            if(!sz.get(4).toString().equals(""))
                szAr.setTitle(sz.get(4).toString());

            sizeOut.add(szAr);
        }
        return sizeOut;
    }
    private List<ProductCount> convertCount(List<List<Object>> size) {
        List<ProductCount> countOut = new ArrayList<>();
        for (List<Object> sz : size)
        {
            if(sz.size() == 0)
                continue;
            ProductCount coAr = new ProductCount();
            //Устанавливаем id товара
            if(!sz.get(0).toString().equals(""))
                coAr.setProductId(Integer.parseInt(sz.get(0).toString()));
            //Устанавливаем уникальный для товара id количества
            if(!sz.get(1).toString().equals(""))
                coAr.setNumber(Integer.parseInt(sz.get(1).toString()));
            //Устанавливаем тип действия
            if(!sz.get(2).toString().equals(""))
                coAr.setAction(sz.get(2).toString());
            //Устанавливаем влияние на общее количество
            if(!sz.get(3).toString().equals(""))
                coAr.setCount(Integer.parseInt(sz.get(3).toString()));
            //Устанавливаем заголовок размера
            if(!sz.get(4).toString().equals(""))
                coAr.setTitle(sz.get(4).toString());

            countOut.add(coAr);
        }
        return countOut;
    }
    private StringBuilder catalogExecute(List<Catalog> table, List<Catalog> db) {
        StringBuilder update = new StringBuilder();

        db.stream().filter(x -> {
            List<Catalog> dqe = table.stream().filter(p -> p.getId() != null && p.getId().equals(x.getId())).toList();
            if (dqe.size() > 0)
                return false;
            else {
                update.append("Catalog: ");
                update.append(x.getId());
                update.append(" будет удаленн!");
                update.append("\r\n");
                catalogDAO.delete(x);
                return true;
            }
        });

        for (Catalog cat : table) {
            if (cat.getId() == null) {
                //Создаем новый каталог
                update.append("Будет созданн новый каталог: ").append(cat);
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
    private StringBuilder productExecute(List<Product> table, List<ProductSize> size, List<ProductCount> count, List<Product> db) {
        StringBuilder update = new StringBuilder();

        db.stream().filter(x -> {
            List<Product> dqe = table.stream().filter(p -> p.getId() > 0 && p.getId() == x.getId()).toList();
            if (dqe.size() > 0)
                return false;
            else {
                update.append("Товар: ");
                update.append(x.getId());
                update.append(" будет удаленн!");
                update.append("\r\n");
                productDAO.delete(x.getId());
                return true;
            }
        });

        for (Product prod : table) {
            //Добавляем в продукт свойства размера
            prod.setProductSize(size);
            //Добавляем в продукт свойства количества
            prod.setProductCount(count);
            //Если нет id
            if (prod.getId() == 0) {
                //Создаем новый каталог
                update.append("2 Будет созданн новый товар:\r\n").append(prod);
                productDAO.set(prod);
            }
            // Если есть id
            else {
                //Получаем продук который есть в базе
                Product prodDb = productDAO.get(prod.getId());
                //Выводим информацию о изменения
                String upd = prodDb.check(prod).toString();
                //Если есть изменения
                if(!upd.equals("")) {
                    productDAO.set(prod);
                    update.append(upd);
                    update.append("\r\n");
                }
            }
        }
        return update;
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
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        //Изменяем каталоги
        updates.append(catalogExecute(convertCatalog(catalog), catalogDAO.getCatalogAllStore((long) shopid)));
        updates.append(productExecute(convertProduct(product), convertSize(size), convertCount(count), productDAO.getStore((long) shopid)));

        if(updates.length() < 5)
        {
            updates.append("Текущий список актуален ✅");
        }
        getStringBuilder(updates);
    }
}
