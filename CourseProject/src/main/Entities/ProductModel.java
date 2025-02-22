package main.Entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductModel {
    private int productId;
    private String productName;
    private String productCategory;
    private String manufacturer;
    private double price;
    private int stockQuantity;
    private String image;

    public ProductModel(int productId, String productName, String productCategory, String manufacturer, double price, int stockQuantity, String image) {
        this.productId = productId;
        this.productName = productName;
        this.productCategory = productCategory;
        this.manufacturer = manufacturer;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.image = image;
    }

    public ProductModel() {}

    public ProductModel(int id) {this.productId = id;}

    @Override
    public String toString() {
        return "Product [productId=" + productId + ", productName=" + productName + ", productCategory=" + productCategory + ", manufacturer=" + manufacturer + ", price=" + price + ", stockQuantity=" + stockQuantity + ", image=" + image + "]";
    }
}