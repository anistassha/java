package main.java.controllers;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import main.ClientSocket;
import main.Enums.RequestType;
import main.Enums.ResponseStatus;
import main.TCP.Request;
import main.TCP.Response;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import main.Entities.ProductModel;

public class LowStockController implements Initializable {

    @FXML
    private Label lowStockLabel; // Label для отображения товаров с низким количеством

    private static final Gson GSON = new Gson();
    private static final int CHECK_INTERVAL_SECONDS = 30; // Интервал проверки в секундах
    private ScheduledExecutorService scheduler;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Запуск периодической проверки
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::checkLowStock, 0, CHECK_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    private void checkLowStock() {
        Request request = new Request();
        request.setRequestType(RequestType.GET_LOW_STOCK_ITEMS);

        try {
            // Отправляем запрос на сервер
            ClientSocket.getInstance().getOutStream().println(GSON.toJson(request));
            ClientSocket.getInstance().getOutStream().flush();

            // Читаем ответ от сервера
            String responseJson = ClientSocket.getInstance().getInStream().readLine();
            if (responseJson == null || responseJson.isEmpty()) {
                throw new RuntimeException("Ответ от сервера пустой");
            }

            // Парсим ответ
            Response response = GSON.fromJson(responseJson, Response.class);
            if (response.getResponseStatus() == ResponseStatus.OK) {
                String lowStockItemsJson = response.getResponseData();
                ProductModel[] lowStockItems = GSON.fromJson(lowStockItemsJson, ProductModel[].class);

                // Обновляем текст Label на JavaFX Application Thread
                Platform.runLater(() -> updateLowStockLabel(lowStockItems));
            } else {
                Platform.runLater(() -> showAlert("Ошибка", response.getResponseMessage()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> showAlert("Ошибка", "Не удалось подключиться к серверу!"));
        }
    }

    private void updateLowStockLabel(ProductModel[] products) {
        if (products.length == 0) {
            lowStockLabel.setText("Все товары в достаточном количестве.");
        } else {
            StringBuilder text = new StringBuilder("Товары с низким количеством:\n");
            Arrays.stream(products).forEach(product ->
                    text.append(product.getProductName()).append(" (").append(product.getStockQuantity()).append(" шт.)\n")
            );
            lowStockLabel.setText(text.toString());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void finalize() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}
