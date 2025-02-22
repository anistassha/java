package main.Entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonData {
    private int personId;
    private String name;
    private String surname;
    private String email;

    public PersonData() {}

    public PersonData(int personId) {
        this.personId = personId;
    }

    public PersonData(int personId, String name, String surname, String email) {
        this.personId = personId;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    @Override
    public String toString() {
        return "PersonData{" +
                "personId=" + personId +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
