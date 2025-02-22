package org.example.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private int employeeId;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private PersonData person;

    @Column(name = "status")
    private String status;

    public Employee(){}

    public Employee(int employeeIdId, String login, String password, String role, PersonData person, String status) {
        this.employeeId = employeeIdId;
        this.login = login;
        this.password = password;
        this.role = role;
        this.person = person;
        this.status = status;
    }
}