package com.example.tbotspring;


import org.hibernate.annotations.NaturalId;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Users")
public class UserBot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @NaturalId
    private Long tgId;
    private String firstName;
    private Boolean isBot;
    private String lastName;
    private String userName;
    private String languageCode;
    private Boolean canJoinGroups;
    private Boolean canReadAllGroupMessages;
    private Boolean supportInlineQueries;
    private Boolean isPremium;
    private Boolean addedToAttachmentMenu;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "userid")
    private List<Store> stores;
    @Transient
    private User user;

    public UserBot() {
    }

    public UserBot(Long tgId, String firstName, Boolean isBot, String lastName, String userName, String languageCode, Boolean canJoinGroups, Boolean canReadAllGroupMessages, Boolean supportInlineQueries, Boolean isPremium, Boolean addedToAttachmentMenu, List<Store> stores) {
        user = new User(tgId,firstName,isBot,lastName,userName,languageCode,canJoinGroups,canReadAllGroupMessages,supportInlineQueries,isPremium,addedToAttachmentMenu);
        this.tgId = tgId;
        this.firstName = firstName;
        this.isBot = isBot;
        this.lastName = lastName;
        this.userName = userName;
        this.languageCode = languageCode;
        this.canJoinGroups = canJoinGroups;
        this.canReadAllGroupMessages = canReadAllGroupMessages;
        this.supportInlineQueries = supportInlineQueries;
        this.isPremium = isPremium;
        this.addedToAttachmentMenu = addedToAttachmentMenu;
        this.stores = stores;
    }

    public Store createStoreUser(String title)
    {
        if(stores == null)
            stores = new ArrayList<>();
        Store store = new Store(this.id,title);
        stores.add(dataBase.saveStore(store));
        return store;
    }
    public void updateUser()
    {
        dataBase.updateUser(this);
    }

    @Override
    public String toString() {
        return "UserBot{" +
                "id=" + id +
                ", tgId=" + tgId +
                ", firstName='" + firstName + '\'' +
                ", isBot=" + isBot +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", languageCode='" + languageCode + '\'' +
                ", canJoinGroups=" + canJoinGroups +
                ", canReadAllGroupMessages=" + canReadAllGroupMessages +
                ", supportInlineQueries=" + supportInlineQueries +
                ", isPremium=" + isPremium +
                ", addedToAttachmentMenu=" + addedToAttachmentMenu +
                ", stores=" + stores +
                '}';
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Store> getStores() {
        //stores = dataBase.getUserStores(this.tgId);
        if (stores == null)
            stores = new ArrayList<>();

        return stores;
    }
    public Store deleteStore(Store store)
    {
        if(this.id == store.getUserid()) {
            stores.removeIf(x -> x.getId() == store.getId());
            this.updateUser();
        }
        else
        {
            System.out.println("Попытка удаления чужого магазина!");
        }
        return store;
    }
    public Store updateStore(Store store)
    {
        if(this.id == store.getUserid()) {

            Store.updateStore(store);
            this.updateUser();
        }
        else
        {
            System.out.println("Попытка редактирования чужого магазина!");
        }
        return store;
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
    }

    public Long getTgId() {
        return tgId;
    }

    public void setTgId(Long id) {
        this.tgId = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Boolean getBot() {
        return isBot;
    }

    public void setBot(Boolean bot) {
        isBot = bot;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public Boolean getCanJoinGroups() {
        return canJoinGroups;
    }

    public void setCanJoinGroups(Boolean canJoinGroups) {
        this.canJoinGroups = canJoinGroups;
    }

    public Boolean getCanReadAllGroupMessages() {
        return canReadAllGroupMessages;
    }

    public void setCanReadAllGroupMessages(Boolean canReadAllGroupMessages) {
        this.canReadAllGroupMessages = canReadAllGroupMessages;
    }

    public Boolean getSupportInlineQueries() {
        return supportInlineQueries;
    }

    public void setSupportInlineQueries(Boolean supportInlineQueries) {
        this.supportInlineQueries = supportInlineQueries;
    }

    public Boolean getPremium() {
        return isPremium;
    }

    public void setPremium(Boolean premium) {
        isPremium = premium;
    }

    public Boolean getAddedToAttachmentMenu() {
        return addedToAttachmentMenu;
    }

    public void setAddedToAttachmentMenu(Boolean addedToAttachmentMenu) {
        this.addedToAttachmentMenu = addedToAttachmentMenu;
    }
}
