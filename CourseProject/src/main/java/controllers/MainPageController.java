package main.java.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import main.java.others.CurrencyAPI;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {
    @FXML
    private Label label_usdToEur;

    @FXML
    private Label label_usdToRub;

    @FXML
    private Label label_usdToByn;

    @FXML
    private void updateExchangeRates() {
        String jsonResponse = CurrencyAPI.getExchangeRates();
        if (jsonResponse != null) {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonObject rates = jsonObject.getAsJsonObject("conversion_rates");

            double usdToEur = rates.get("EUR").getAsDouble();
            double usdToRub = rates.get("RUB").getAsDouble();
            double usdToByn = rates.get("BYN").getAsDouble();

            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            label_usdToEur.setText(decimalFormat.format(usdToEur));
            label_usdToRub.setText(decimalFormat.format(usdToRub));
            label_usdToByn.setText(decimalFormat.format(usdToByn));
        } else {
            label_usdToEur.setText("Ошибка загрузки данных");
            label_usdToRub.setText("Ошибка загрузки данных");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateExchangeRates();
    }

}
