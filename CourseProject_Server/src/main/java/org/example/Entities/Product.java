package org.example.Entities;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product")
public class Product {
    @Id
    @Column(name = "product_id")
    private int productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_category")
    private String productCategory;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "price")
    private double price;

    @Column(name = "stock_quantity")
    private int stockQuantity;

    @Column(name = "image")
    private String image;

    public Product(int productId, String productName, String productCategory, String manufacturer, double price, int stockQuantity, String image) {
        this.productId = productId;
        this.productName = productName;
        this.productCategory = productCategory;
        this.manufacturer = manufacturer;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.image = image;
    }

    public Product() {}

    public Product(int id) {
        this.productId = id;
    }
}

