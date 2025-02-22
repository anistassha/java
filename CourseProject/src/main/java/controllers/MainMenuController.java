package main.java.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.ClientSocket;
import main.Entities.Employee;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {
    @FXML
    private Button btnAccounts;

    @FXML
    private Button btnMyAccount;

    @FXML
    private Button btnCustomers;

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnInventoryItem;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnReports;

    @FXML
    private Button btnSells;

    @FXML
    private Label lblAccessLevel;

    @FXML
    private Label lblUsername;

    @FXML
    private AnchorPane paneAccountSection;

    @FXML
    private AnchorPane paneLeft;

    @FXML
    private AnchorPane paneMenuHolder;

    @FXML
    private AnchorPane paneRight;

    @FXML
    public ImageView lowStock_img;

    @FXML
    private Button btnLowStock;

    private Button temp = null;
    private HashMap<String, String> FXML_URL = new HashMap<>();
    private AnchorPane newRightPane = null;

    public void setEmployeeData(Employee employee) {
        if (employee != null) {
            lblUsername.setText(employee.getLogin());
            lblAccessLevel.setText(employee.getRole());
        }
    }

    @FXML
    void btnNavigators(ActionEvent event) {
        borderSelector(event);

        Button btn = (Button)event.getSource();
        String btnText = btn.getText();

        try {
            ctrlRightPane(FXML_URL.get(btnText));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void borderSelector(ActionEvent event) {
        Button btn = (Button)event.getSource();

        if(temp == null) {
            temp = btn;
        } else {
            temp.setStyle("-fx-background-color: #3a3a3a; -fx-text-fill: white");
            temp = btn;
        }

        btn.setStyle("-fx-background-color: #455A64; -fx-text-fill: white");
    }

    private void loadFXMLMap() {
        FXML_URL.put("Главная", "/main/resourses/view/main_page.fxml");
        FXML_URL.put("Товары", "/main/resourses/view/items.fxml");
        FXML_URL.put("Покупатели", "/main/resourses/view/customers.fxml");
        FXML_URL.put("Продажи", "/main/resourses/view/purchases.fxml");
        FXML_URL.put("Сотрудники", "/main/resourses/view/employee.fxml");
        FXML_URL.put("Мой аккаунт", "/main/resourses/view/myaccount.fxml");
        FXML_URL.put("Отчёты", "/main/resourses/view/reports.fxml");
        FXML_URL.put("Уведомления", "/main/resourses/view/lowstock.fxml");
    }

    @FXML
    private void ctrlRightPane(String URL) throws IOException {
        try {
            paneRight.getChildren().clear();
            newRightPane = FXMLLoader.load(getClass().getResource(URL));

            AnchorPane.setTopAnchor(newRightPane, 0.0);
            AnchorPane.setRightAnchor(newRightPane, 0.0);
            AnchorPane.setBottomAnchor(newRightPane, 0.0);
            AnchorPane.setLeftAnchor(newRightPane, 0.0);

            newRightPane.setPrefHeight(paneRight.getHeight());
            newRightPane.setPrefWidth(paneRight.getWidth());

            paneRight.getChildren().add(newRightPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (ClientSocket.getInstance().getEmployee().getRole().equals("Администратор")) {
            btnAccounts.setVisible(true);
        } else btnAccounts.setVisible(false);

        if (ClientSocket.getInstance().getEmployee().getRole().equals("Администратор")) {
            btnReports.setVisible(true);
        } else btnReports.setVisible(false);

//        if (ClientSocket.getInstance().getEmployee().getRole().equals("Администратор")) {
//            btnLowStock.setVisible(true);
//        } else btnReports.setVisible(false);

        loadFXMLMap();
        temp = btnDashboard;
        temp.setStyle("-fx-background-color: #455A64; -fx-text-fill: white");

        Employee employee = ClientSocket.getInstance().getEmployee();

        lblUsername.setText(employee.getLogin());
        lblAccessLevel.setText(employee.getRole());

        employee.addLoginChangeListener(() -> lblUsername.setText(employee.getLogin()));

        try {
            ctrlRightPane("/main/resourses/view/main_page.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logOut() {
        Stage current = (Stage)lblUsername.getScene().getWindow();
        current.close();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/main/resourses/view/login.fxml"));
            Scene scene = new Scene(root);
            Stage logIn = new Stage();
            logIn.setTitle("PowerHouse");
            logIn.setScene(scene);
            logIn.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

