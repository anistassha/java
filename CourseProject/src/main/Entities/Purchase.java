package main.Entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Purchase {
    private int purchaseId;
    private ProductModel product;
    private Customer customer;
    private Employee employee;
    private String date;
    private int quantity;
    private double payAmount;

    public Purchase(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Purchase(int purchaseId, ProductModel product, Customer customer, Employee employee, String date, int quantity, double payAmount) {
        this.purchaseId = purchaseId;
        this.product = product;
        this.customer = customer;
        this.employee = employee;
        this.date = date;
        this.quantity = quantity;
        this.payAmount = payAmount;
    }

    public Purchase() {}
}

