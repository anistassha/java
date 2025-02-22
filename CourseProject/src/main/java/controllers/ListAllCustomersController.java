package main.java.controllers;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.ClientSocket;
import main.Entities.Customer;
import main.Entities.ProductModel;
import main.Enums.RequestType;
import main.Enums.ResponseStatus;
import main.TCP.Request;
import main.TCP.Response;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ListAllCustomersController implements Initializable {

    @FXML
    private Button customers_back;

    @FXML
    private TableColumn<Customer, String> customers_col_customerEmail;

    @FXML
    private TableColumn<Customer, Number> customers_col_customerId;

    @FXML
    private TableColumn<Customer, String> customers_col_customerName;

    @FXML
    private TableColumn<Customer, String> customers_col_customerNumber;

    @FXML
    private TableColumn<Customer, String> customers_col_customerSex;

    @FXML
    private TableColumn<Customer, String> customers_col_customerSurname;

    @FXML
    private TableView<Customer> customers_tableView;

    private static final Gson GSON = new Gson();

    private void loadCustomers() {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.GET_ALL_CUSTOMERS);

            ClientSocket.getInstance().getOutStream().println(GSON.toJson(request));
            ClientSocket.getInstance().getOutStream().flush();

            String answer = ClientSocket.getInstance().getInStream().readLine();
            if (answer == null || answer.isEmpty()) {
                throw new RuntimeException("Ответ от сервера пустой");
            }

            Response response = GSON.fromJson(answer, Response.class);
            if (response == null) {
                throw new RuntimeException("Ответ от сервера не может быть преобразован в объект Response");
            }

            if (response.getResponseStatus() == ResponseStatus.OK) {
                String customersJson = response.getResponseData();
                Customer[] customersArray = GSON.fromJson(customersJson, Customer[].class);

                ObservableList<Customer> customers = FXCollections.observableArrayList(customersArray);
                customers_tableView.setItems(customers);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Ошибка загрузки данных!");
                alert.showAndWait();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void backToCustomers() {
        try {
            Scene currentScene = ((Stage) customers_back.getScene().getWindow()).getScene();
            AnchorPane paneRight = (AnchorPane) currentScene.lookup("#paneRight");

            Parent customersPane = FXMLLoader.load(getClass().getResource("/main/resourses/view/customers.fxml"));

            paneRight.getChildren().clear();
            paneRight.getChildren().add(customersPane);

            AnchorPane.setTopAnchor(customersPane, 0.0);
            AnchorPane.setBottomAnchor(customersPane, 0.0);
            AnchorPane.setLeftAnchor(customersPane, 0.0);
            AnchorPane.setRightAnchor(customersPane, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customers_col_customerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customers_col_customerName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        customers_col_customerSurname.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        customers_col_customerNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        customers_col_customerEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        customers_col_customerSex.setCellValueFactory(new PropertyValueFactory<>("gender"));

        loadCustomers();
    }

}
