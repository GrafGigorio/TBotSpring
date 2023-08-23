package ru.masich.bot.entity;


import javax.persistence.*;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "shop")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private Long userid;
    private String title;
    private String tableID;
    private String chartID;
    private String folderID;
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> role;

    public Store(Long userid, String title,String folderID, String tableID, String chartID, Map<String, Object> role) {
        this.userid = userid;
        this.title = title;
        this.tableID = tableID;
        this.chartID = chartID;
        this.folderID = folderID;
        this.role = role;
    }
    public Store(Long userid, String title, Map<String, Object> role) {
        this.userid = userid;
        this.title = title;
        this.role = role;
    }

    public Store() {

    }

    @Override
    public String toString() {
        return "Store{" +
                "id=" + id +
                ", userid=" + userid +
                ", title='" + title + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Object> getRole() {
        return role;
    }

    public void setRole(Map<String, Object> role) {
        this.role = role;
    }

    public String getTableID() {
        return tableID;
    }

    public void setTableID(String tableID) {
        this.tableID = tableID;
    }

    public String getChartID() {
        return chartID;
    }

    public void setChartID(String chartID) {
        this.chartID = chartID;
    }

    public String getFolderID() {
        return folderID;
    }

    public void setFolderID(String folderID) {
        this.folderID = folderID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return Objects.equals(id, store.id) && Objects.equals(userid, store.userid) && Objects.equals(title, store.title) && Objects.equals(tableID, store.tableID) && Objects.equals(chartID, store.chartID) && Objects.equals(folderID, store.folderID) && Objects.equals(role, store.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userid, title, tableID, chartID, folderID, role);
    }
}
