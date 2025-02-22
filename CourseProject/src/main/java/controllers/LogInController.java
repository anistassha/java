package main.java.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.ClientSocket;
import main.Entities.Employee;
import main.Enums.RequestType;
import main.Enums.ResponseStatus;
import main.Main;
import main.TCP.Request;

import com.google.gson.Gson;
import main.TCP.Response;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

import main.java.others.PasswordUtils;

public class LogInController {
    private static final Gson GSON = new Gson();

    @FXML
    private Button button_login;
    @FXML
    private TextField tf_login;
    @FXML
    private PasswordField tf_password;
    @FXML
    private Label lbl_warn_login;
    @FXML
    private Label lbl_warn_password;
    @FXML
    private Label button_goToReg;

    @FXML
    public void initialize() {
        button_login.setOnAction(event -> {
            String login = tf_login.getText();
            String password = tf_password.getText();

            if (login.isEmpty()) {
                lbl_warn_login.setVisible(true);
            } else if (password.isEmpty()) {
                lbl_warn_password.setVisible(true);
            } else {
                try {
                    Employee employee = new Employee();
                    employee.setLogin(login);
                    employee.setPassword(password);

                    Request request = new Request();
                    request.setRequestMessage(GSON.toJson(employee));
                    request.setRequestType(RequestType.LOGIN);

                    ClientSocket.getInstance().getOutStream().println(GSON.toJson(request));
                    ClientSocket.getInstance().getOutStream().flush();

                    String answer = ClientSocket.getInstance().getInStream().readLine();
                    System.out.println("Received JSON: " + answer);
                    Response response = GSON.fromJson(answer, Response.class);

                    if (response.getResponseStatus() == ResponseStatus.OK) {
                        String employeeJson = response.getResponseData();
                        System.out.println("employeeJson: " + employeeJson);
                        Employee authenticatedEmployee = GSON.fromJson(employeeJson, Employee.class);

                        if (authenticatedEmployee.getStatus() == 1) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("К сожалению, ваш аккаунт был заблокирован.");
                            alert.showAndWait();
                        } else {
                            ClientSocket.getInstance().setEmployee(authenticatedEmployee);

                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main/resourses/view/main_menu.fxml"));
                            Parent root = fxmlLoader.load();

//                            MainMenuController mainMenuController = fxmlLoader.getController();
//                            mainMenuController.setEmployeeData(authenticatedEmployee);

                            Stage stage = (Stage) button_login.getScene().getWindow();
                            stage.setScene(new Scene(root));
                            stage.centerOnScreen();
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Ошибка авторизации!");
                        alert.showAndWait();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        tf_login.setOnMouseClicked(event -> {
            lbl_warn_login.setVisible(false);
        });

        tf_password.setOnMouseClicked(event -> {
            lbl_warn_password.setVisible(false);
        });
    }

    @FXML
    void goToRegistration() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resourses/view/signup.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) button_login.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
