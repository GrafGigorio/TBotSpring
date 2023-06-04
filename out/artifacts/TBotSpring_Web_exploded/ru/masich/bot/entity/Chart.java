package ru.masich.bot.entity;

import javax.persistence.*;
import java.util.Map;

@Entity
@Table(name = "chart")
public class Chart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private Long user_id;
    private Boolean chart_active;
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> data;

    public Chart() {
    }

    public Chart(Long user_id, Boolean chart_active, Map<String, Object> data) {
        this.user_id = user_id;
        this.chart_active = chart_active;
        this.data = data;
    }

    @Override
    public String toString() {
        return "Chart{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", chart_active=" + chart_active +
                ", data=" + data +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Boolean getChart_active() {
        return chart_active;
    }

    public void setChart_active(Boolean chart_active) {
        this.chart_active = chart_active;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
