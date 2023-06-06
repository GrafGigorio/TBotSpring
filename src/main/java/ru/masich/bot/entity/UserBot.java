package ru.masich.bot.entity;


import org.hibernate.annotations.NaturalId;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

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
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> role;

//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JoinColumn(name = "userid")
//    private LastMessage lastMessage;
    @Transient
    private User user;
//    @Transient
//    private AwaitDao awaitDao = new AwaitDAOimpl();

    public UserBot() {
    }

    public UserBot(Long tgId, String firstName, Boolean isBot, String lastName, String userName, String languageCode, Boolean canJoinGroups, Boolean canReadAllGroupMessages, Boolean supportInlineQueries, Boolean isPremium, Boolean addedToAttachmentMenu, Map<String, Object> role) {
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
        this.role = role;
    }
    public UserBot(User user) {
        this.user = user;
        this.tgId = user.getId();
        this.firstName = user.getFirstName();
        this.isBot = user.getIsBot();
        this.lastName = user.getLastName();
        this.userName = user.getUserName();
        this.languageCode = user.getLanguageCode();
        this.canJoinGroups = user.getCanJoinGroups();
        this.canReadAllGroupMessages = user.getCanReadAllGroupMessages();
        this.supportInlineQueries = user.getSupportInlineQueries();
        this.isPremium = user.getIsPremium();
        this.addedToAttachmentMenu = user.getAddedToAttachmentMenu();
        this.role = new LinkedHashMap<>();
    }


    @Override
    public String toString() {
        return "UserBot{" +
                "\t\r\nid=" + id +
                ",\t\n tgId=" + tgId +
                ",\t\n firstName='" + firstName + '\'' +
                ",\t\n isBot=" + isBot +
                ",\t\n lastName='" + lastName + '\'' +
                ",\t\n userName='" + userName + '\'' +
                ",\t\n languageCode='" + languageCode + '\'' +
                ",\t\n canJoinGroups=" + canJoinGroups +
                ",\t\n canReadAllGroupMessages=" + canReadAllGroupMessages +
                ",\t\n supportInlineQueries=" + supportInlineQueries +
                ",\t\n isPremium=" + isPremium +
                ",\t\n addedToAttachmentMenu=" + addedToAttachmentMenu +
                ",\t\n role=" + role +
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

    public Map<String, Object> getRole() {
        return role;
    }

    public void setRole(Map<String, Object> role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserBot userBot = (UserBot) o;
        return id == userBot.id &&
                Objects.equals(tgId, userBot.tgId) &&
                Objects.equals(firstName, userBot.firstName) &&
                Objects.equals(isBot, userBot.isBot) &&
                Objects.equals(lastName, userBot.lastName) &&
                Objects.equals(userName, userBot.userName) &&
                Objects.equals(languageCode, userBot.languageCode) &&
                Objects.equals(canJoinGroups, userBot.canJoinGroups) &&
                Objects.equals(canReadAllGroupMessages, userBot.canReadAllGroupMessages) &&
                Objects.equals(supportInlineQueries, userBot.supportInlineQueries) &&
                Objects.equals(isPremium, userBot.isPremium) &&
                Objects.equals(addedToAttachmentMenu, userBot.addedToAttachmentMenu) &&
                Objects.equals(role, userBot.role) &&
                Objects.equals(user, userBot.user);
    }

    public boolean equalsUt(User o) {

        if(o == null || o.getClass() != User.class) return false;
        return Objects.equals(tgId, o.getId()) &&
                Objects.equals(firstName, o.getFirstName()) &&
                Objects.equals(isBot, o.getIsBot()) &&
                Objects.equals(lastName, o.getLastName()) &&
                Objects.equals(userName, o.getUserName()) &&
                Objects.equals(languageCode, o.getLanguageCode()) &&
                Objects.equals(canJoinGroups, o.getCanJoinGroups()) &&
                Objects.equals(canReadAllGroupMessages, o.getCanReadAllGroupMessages()) &&
                Objects.equals(supportInlineQueries, o.getSupportInlineQueries()) &&
                Objects.equals(isPremium, o.getIsPremium()) &&
                Objects.equals(addedToAttachmentMenu, o.getAddedToAttachmentMenu());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                tgId,
                firstName,
                isBot,
                lastName,
                userName,
                languageCode,
                canJoinGroups,
                canReadAllGroupMessages,
                supportInlineQueries,
                isPremium,
                addedToAttachmentMenu,
                user);
    }
}
