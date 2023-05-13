package ru.masich.bot.entity;

import java.util.Objects;

public class ProductSize {
    private int productId;
    private int number;
    private String action;
    private boolean defaultSize = false;
    private String title;

    public ProductSize() {
    }

    public ProductSize(int productId, int number, String action, boolean defaultSize, String title) {
        this.productId = productId;
        this.number = number;
        this.action = action;
        this.defaultSize = defaultSize;
        this.title = title;
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

    public boolean isDefaultSize() {
        return defaultSize;
    }

    public void setDefaultSize(boolean defaultSize) {
        this.defaultSize = defaultSize;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductSize that = (ProductSize) o;
        return productId == that.productId && number == that.number && defaultSize == that.defaultSize && Objects.equals(action, that.action) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, number, action, defaultSize, title);
    }
}
