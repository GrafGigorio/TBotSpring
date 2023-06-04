package ru.masich.bot;


import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Var {
    public static final String createStore = "/create_store";
    public static final String getMyStores = "/get_my_shops";
    public static final String startMenu = "/start_menu";
    public static final String getStartMenuTitle = "Главное меню";
    public static final String getStoresTitle = "Мои магазины";
    public static final String storeGet = "store:get:";
    public static final String storeEdit = "store:edit:";
    public static final String storeDelete = "store:delete:";
    public static final String catalogCreate = "catalog:create:";
    public static final String catalogGet = "catalog:get:";
    public static final String catalogGetMasterTitle = "Список каталогов";
    public static final String catalogGetChildTitle = "Список дочерних каталогов";
    public static final String catalogEdit = "catalog:edit:";
    public static final String catalogDelete = "catalog:delete:";
    public static final String productGet = "product:get:";
    public static final String productGetList = "product:getList:";
    public static final String productCreate = "product:create:";
    public static final String productEdit = "product:edit:";
    public static final String productDelete = "product:delete:";


    public static SessionFactory sessionFactory = getSessionFactory();
    public static SessionFactory getSessionFactory()
    {
        return new Configuration()
                .configure()
                .buildSessionFactory();
    }
}
