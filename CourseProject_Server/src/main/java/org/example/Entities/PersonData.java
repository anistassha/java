package org.example.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "persondata")
public class PersonData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private int personId;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "email")
    private String email;

    public PersonData() {}

    public PersonData(int personId, String name, String surname, String email) {
        this.personId = personId;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }
}
