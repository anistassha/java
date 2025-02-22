package main.java.controllers;

import com.google.gson.Gson;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import main.ClientSocket;
import main.Entities.Customer;
import main.Entities.Purchase;
import main.Enums.RequestType;
import main.Enums.ResponseStatus;
import main.TCP.Request;
import main.TCP.Response;
import main.java.others.DBConnection;
import main.java.others.WordReceiptGenerator;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class EmployeePurchaseController implements Initializable {

    @FXML
    private NumberAxis amountAxis;

    @FXML
    private Label cp_countPurchases;

    @FXML
    private Label cp_totalAmount;

    @FXML
    private Button emppur_btn;

    @FXML
    private CategoryAxis dateAxis;

    @FXML
    private TableColumn<Purchase, Number> employeePurchase_col_amount;

    @FXML
    private TableColumn<Purchase, Number> employeePurchase_col_customerID;

    @FXML
    private TableColumn<Purchase, String> employeePurchase_col_date;

    @FXML
    private TableColumn<Purchase, Number> employeePurchase_col_productId;

    @FXML
    private TableColumn<Purchase, Number> employeePurchase_col_purchaseId;

    @FXML
    private TableColumn<Purchase, Number> employeePurchase_col_quantity;

    @FXML
    private TableView<Purchase> employeePurchase_tableView;

    @FXML
    private LineChart<String, Integer> lineChart;

    @FXML
    private ImageView emppur_grafikImg;

    @FXML
    private ImageView emppur_tableImg;

    private static final Gson GSON = new Gson();
    private static boolean toggleTable = true;
    private Purchase[] purchases;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        employeePurchase_col_purchaseId.setCellValueFactory(new PropertyValueFactory<>("purchaseId"));
//        customerPurchase_col_productId.setCellValueFactory(new PropertyValueFactory<>("product"));
//        customerPurchase_col_employee.setCellValueFactory(new PropertyValueFactory<>("employee"));
        employeePurchase_col_productId.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getProduct().getProductId()));
        employeePurchase_col_customerID.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCustomer().getCustomerId()));
        employeePurchase_col_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        employeePurchase_col_amount.setCellValueFactory(new PropertyValueFactory<>("payAmount"));
        employeePurchase_col_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        Request request = new Request();
        request.setRequestType(RequestType.SEARCH_PURCHASE_BY_EMPLOYEE_ID);
        request.setRequestMessage(String.valueOf(ClientSocket.getInstance().getEmployee().getEmployeeId()));

        try {
            ClientSocket.getInstance().getOutStream().println(GSON.toJson(request));
            ClientSocket.getInstance().getOutStream().flush();

            String responseJson = ClientSocket.getInstance().getInStream().readLine();
            if (responseJson == null || responseJson.isEmpty()) {
                throw new RuntimeException("Ответ от сервера пустой");
            }

            Response response = GSON.fromJson(responseJson, Response.class);
            if (response.getResponseStatus() == ResponseStatus.OK) {
                String purchasesJson = response.getResponseData();
                purchases = GSON.fromJson(purchasesJson, Purchase[].class);

                ObservableList<Purchase> purchases1 = FXCollections.observableArrayList(purchases);
                employeePurchase_tableView.setItems(purchases1);

                cp_countPurchases.setText(String.valueOf(purchases.length));
                double totalAmount = 0;
                for (Purchase purchase : purchases) {
                    totalAmount += purchase.getPayAmount();  
                }

                cp_totalAmount.setText(String.format("%.2f BYN", totalAmount));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Ошибка при получении данных о покупках!");
            alert.showAndWait();
        }
    }

    @FXML
    void btnBarchartAction() {
        if(toggleTable) {
            toggleTable = false;
            emppur_grafikImg.setVisible(false);
            emppur_tableImg.setVisible(true);
            employeePurchase_tableView.setVisible(false);
            lineChart.setVisible(true);

            generateLineChart();
        } else {
            toggleTable = true;
            emppur_grafikImg.setVisible(true);
            emppur_tableImg.setVisible(false);
            lineChart.setVisible(false);
            lineChart.getData().clear();
            employeePurchase_tableView.setVisible(true);
        }
    }

    void generateLineChart() {
        lineChart.getData().clear();

        Map<String, Double> dailySales = new HashMap<>();

        for (Purchase purchase : purchases) {
            String date = purchase.getDate().toString();
            double payAmount = purchase.getPayAmount();

            dailySales.put(date, dailySales.getOrDefault(date, 0.0) + payAmount);
        }

        XYChart.Series<String, Integer> chartData = new XYChart.Series<>();

        dailySales.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> chartData.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue().intValue())));

        lineChart.getData().add(chartData);
    }

    @FXML
    void saveReceiptAction() {
        Purchase selectedPurchase = employeePurchase_tableView.getSelectionModel().getSelectedItem();

        if (selectedPurchase == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Пожалуйста, выберите покупку для генерации чека.");
            alert.showAndWait();
            return;
        }

        String filePath = "receipts/receipt_" + selectedPurchase.getPurchaseId() + ".docx";
        WordReceiptGenerator.generateReceipt(new Purchase[]{selectedPurchase}, filePath);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Чек успешно сохранен по пути: " + filePath);
        alert.showAndWait();
    }
}
