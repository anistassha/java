package main.Entities;

import javafx.beans.property.*;

public class Product {
    private final SimpleIntegerProperty productId;
    private final SimpleStringProperty productName;
    private final SimpleStringProperty productCategory;
    private final SimpleStringProperty manufacturer;
    private final SimpleDoubleProperty price;
    private final SimpleIntegerProperty stockQuantity;
    private final SimpleStringProperty image;

    public Product(int productId, String productName, String productCategory, String manufacturer, double price, int stockQuantity, String image) {
        this.productId = new SimpleIntegerProperty(productId);
        this.productName = new SimpleStringProperty(productName);
        this.productCategory = new SimpleStringProperty(productCategory);
        this.manufacturer = new SimpleStringProperty(manufacturer);
        this.price = new SimpleDoubleProperty(price);
        this.stockQuantity = new SimpleIntegerProperty(stockQuantity);
        this.image = new SimpleStringProperty(image);
    }

    public Product() {
        this(0, "", "", "", 0.0, 0, "");
    }

    public Product(int id) {
        this(id, "", "", "", 0.0, 0, "");
    }

    public int getProductId() {
        return productId.get();
    }

    public void setProductId(int productId) {
        this.productId.set(productId);
    }

    public SimpleIntegerProperty productIdProperty() {
        return productId;
    }

    public String getProductName() {
        return productName.get();
    }

    public void setProductName(String productName) {
        this.productName.set(productName);
    }

    public SimpleStringProperty productNameProperty() {
        return productName;
    }

    public String getProductCategory() {
        return productCategory.get();
    }

    public void setProductCategory(String productCategory) {
        this.productCategory.set(productCategory);
    }

    public SimpleStringProperty productCategoryProperty() {
        return productCategory;
    }

    public String getManufacturer() {
        return manufacturer.get();
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer.set(manufacturer);
    }

    public SimpleStringProperty manufacturerProperty() {
        return manufacturer;
    }

    public double getPrice() {
        return price.get();
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity.get();
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity.set(stockQuantity);
    }

    public SimpleIntegerProperty stockQuantityProperty() {
        return stockQuantity;
    }

    public String getImage() {
        return image.get();
    }

    public void setImage(String image) {
        this.image.set(image);
    }

    public SimpleStringProperty imageProperty() {
        return image;
    }

    @Override
    public String toString() {
        return "Product [productId=" + getProductId() +
                ", productName=" + getProductName() +
                ", productCategory=" + getProductCategory() +
                ", manufacturer=" + getManufacturer() +
                ", price=" + getPrice() +
                ", stockQuantity=" + getStockQuantity() +
                ", image=" + getImage() + "]";
    }
}
