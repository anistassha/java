package main.Entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Customer {
    private int customerId;
    private String firstName;
    private String lastName;
    private String gender;
    private String phoneNumber;
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

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
