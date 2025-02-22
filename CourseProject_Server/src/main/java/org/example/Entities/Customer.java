package org.example.Entities;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @Column(name = "customer_id")
    private int customerId;

    @Column(name = "customer_firstName")
    private String firstName;

    @Column(name = "customer_lastName")
    private String lastName;

    @Column(name = "customer_gender")
    private String gender;

    @Column(name = "customer_phoneNumber")
    private String phoneNumber;

    @Column(name = "customer_email")
    private String email;

    public Customer() {}

    public Customer(int id) {
        this.customerId = id;
    }

    public Customer(int id, String firstName, String lastName, String gender, String phoneNumber, String email) {
        this.customerId = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
}

