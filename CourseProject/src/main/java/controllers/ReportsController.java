package main.java.controllers;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import main.ClientSocket;
import main.Entities.Purchase;
import main.Enums.RequestType;
import main.Enums.ResponseStatus;
import main.TCP.Request;
import main.TCP.Response;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {

    @FXML
    private NumberAxis reports_amountAxis;

    @FXML
    private BarChart<String, Integer> reports_barChart;

    @FXML
    private CategoryAxis reports_dateAxis;

    @FXML
    private CategoryAxis reports_dateAxis_qua;

    @FXML
    private LineChart<String, Integer> reports_lineChart;

    @FXML
    private NumberAxis reports_quantityAxis;

    private static final Gson GSON = new Gson();
    private Purchase[] purchases;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Request request = new Request();
        request.setRequestType(RequestType.GET_ALL_PURCHASES);
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
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Ошибка при получении данных о покупках!");
            alert.showAndWait();
        }

        generateBarChart();
        generateLineChart();
    }

    void generateBarChart() {
        reports_barChart.getData().clear();

        Map<String, Integer> dailyQuantity = new HashMap<>();

        for (Purchase purchase : purchases) {
            String date = purchase.getDate().toString();
            int quantity = purchase.getQuantity();

            dailyQuantity.put(date, dailyQuantity.getOrDefault(date, 0) + quantity);
        }

        XYChart.Series<String, Integer> chartData = new XYChart.Series<>();

        dailyQuantity.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> chartData.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));

        reports_barChart.getData().add(chartData);
    }

    void generateLineChart() {
        reports_lineChart.getData().clear();

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

        reports_lineChart.getData().add(chartData);
    }
}
