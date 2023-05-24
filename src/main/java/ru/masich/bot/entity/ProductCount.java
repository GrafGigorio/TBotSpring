package ru.masich.bot.entity;

import java.util.Objects;

public class ProductCount {
    private int productId;
    private int number;
    private String action;
    private String title;
    private int count;

    public ProductCount() {
    }

    public ProductCount(int productId, int number,int count, String action, String title) {
        this.productId = productId;
        this.number = number;
        this.action = action;
        this.title = title;
        this.count = count;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductCount that = (ProductCount) o;
        return productId == that.productId && number == that.number && count == that.count && Objects.equals(action, that.action) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, number, action, title, count);
    }
}
