package main.java.controllers;

import com.google.gson.Gson;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.ClientSocket;
import main.Entities.Customer;
import main.Entities.Purchase;
import main.Enums.RequestType;
import main.Enums.ResponseStatus;
import main.TCP.Request;
import main.TCP.Response;
import main.java.others.DBConnection;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class CustomerPurchaseController implements Initializable {

    @FXML
    private Label cp_countPurchases;

    @FXML
    private Label cp_totalAmount;

    @FXML
    private TableColumn<Purchase, Number> customerPurchase_col_amount;

    @FXML
    private TableColumn<Purchase, String> customerPurchase_col_date;

    @FXML
    private TableColumn<Purchase, String> customerPurchase_col_employee;

    @FXML
    private TableColumn<Purchase, Number> customerPurchase_col_productId;

    @FXML
    private TableColumn<Purchase, Number> customerPurchase_col_purchaseId;

    @FXML
    private TableColumn<Purchase, Number> customerPurchase_col_quantity;

    @FXML
    private TableView<Purchase> customerPurchase_tableView;

    public static int customerID = 0;
    private static final Gson GSON = new Gson();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        customerPurchase_col_purchaseId.setCellValueFactory(new PropertyValueFactory<>("purchaseId"));
//        customerPurchase_col_productId.setCellValueFactory(new PropertyValueFactory<>("product"));
//        customerPurchase_col_employee.setCellValueFactory(new PropertyValueFactory<>("employee"));
        customerPurchase_col_productId.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getProduct().getProductId()));
        customerPurchase_col_employee.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEmployee().getLogin()));
        customerPurchase_col_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        customerPurchase_col_amount.setCellValueFactory(new PropertyValueFactory<>("payAmount"));
        customerPurchase_col_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        ObservableList<Purchase> purchases = FXCollections.observableArrayList();

        try {
            Request request = new Request();
            request.setRequestType(RequestType.SEARCH_PURCHASE_BY_CUSTOMER_ID);
            request.setRequestMessage(String.valueOf(customerID));

            ClientSocket.getInstance().getOutStream().println(GSON.toJson(request));
            ClientSocket.getInstance().getOutStream().flush();

            String responseJson = ClientSocket.getInstance().getInStream().readLine();
            if (responseJson == null || responseJson.isEmpty()) {
                throw new RuntimeException("Ответ от сервера пустой");
            }

            Response response = GSON.fromJson(responseJson, Response.class);
            if (response.getResponseStatus() == ResponseStatus.OK) {
                String purchasesJson = response.getResponseData();
                Purchase[] purchasesArray = GSON.fromJson(purchasesJson, Purchase[].class);

                purchases = FXCollections.observableArrayList(purchasesArray);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Ошибка при поиске покупок!");
            alert.showAndWait();
        }

        cp_countPurchases.setText(String.valueOf(purchases.size()));
        double totalAmount = purchases.stream()
                .mapToDouble(Purchase::getPayAmount)
                .sum();
        cp_totalAmount.setText(String.format("%.2f BYN", totalAmount));

        customerPurchase_tableView.setItems(purchases);
    }
}

