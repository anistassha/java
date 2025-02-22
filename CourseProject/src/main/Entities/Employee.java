package main.Entities;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Employee {
    private int employeeId;
    private String login;
    private String password;
    private String role;
    private PersonData person;
    private int status;

    private transient List<Runnable> loginChangeListeners = new ArrayList<>();

    public void setLogin(String login) {
        this.login = login;
        notifyLoginChangeListeners();
    }

    public Employee() {}

    public Employee(int id){
        this.employeeId = id;
    }

    public Employee(int id, String login, String password, String role, PersonData person, int status) {
        this.employeeId = id;
        this.login = login;
        this.password = password;
        this.role = role;
        this.person = person;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    public void addLoginChangeListener(Runnable listener) {
        loginChangeListeners.add(listener);
    }

    private void notifyLoginChangeListeners() {
        for (Runnable listener : loginChangeListeners) {
            listener.run();
        }
    }
}

