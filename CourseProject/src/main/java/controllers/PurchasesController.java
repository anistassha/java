package main.java.controllers;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import main.ClientSocket;
import main.Entities.Customer;
import main.Entities.ProductModel;
import main.Entities.Purchase;
import main.Enums.RequestType;
import main.Enums.ResponseStatus;
import main.TCP.Request;
import main.TCP.Response;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class PurchasesController implements Initializable {
    private static final Gson GSON = new Gson();
    @FXML
    private Button purchases_btnPurchases;

    @FXML
    private Button purchases_confirmBtn;

    @FXML
    private TableColumn<Customer, Number> purchases_col_customerId;

    @FXML
    private TableColumn<Customer, String> purchases_col_customerName;

    @FXML
    private TableColumn<Customer, String> purchases_col_customerSurname;

    @FXML
    private TableColumn<ProductModel, Number> purchases_col_productId;

    @FXML
    private TableColumn<ProductModel, String> purchases_col_productName;

    @FXML
    private TableColumn<ProductModel, Number> purchases_col_productPrice;

    @FXML
    private TableColumn<ProductModel, Number> purchases_col_productQuantity;

    @FXML
    private ImageView purchases_confirmBtnImage;

    @FXML
    private DatePicker purchases_date;

    @FXML
    private Label purchases_lblPrice;

    @FXML
    private TextField purchases_quantity;

    @FXML
    private TableView<Customer> purchases_tableCustomers;

    @FXML
    private TableView<ProductModel> purchases_tableItems;

    private ProductModel productModel;
    private Customer customer;
    private double totalPrice;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        purchases_col_productId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        purchases_col_productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        purchases_col_productPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        purchases_col_productQuantity.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        purchases_col_customerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        purchases_col_customerName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        purchases_col_customerSurname.setCellValueFactory(new PropertyValueFactory<>("lastName"));

//        addProducts_col_productId.setCellValueFactory(cellData -> cellData.getValue().productId());
//        addProducts_col_productName.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
//        addProducts_col_productType.setCellValueFactory(cellData -> cellData.getValue().productCategoryProperty());
//        addProducts_col_productBrand.setCellValueFactory(cellData -> cellData.getValue().manufacturerProperty());
//        addProducts_col_productPrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
//        addProducts_col_productQuantity.setCellValueFactory(cellData -> cellData.getValue().stockQuantityProperty());

        loadProducts();
        loadCustomers();

        this.purchases_confirmBtn.setDisable(true);
        purchases_confirmBtn.setStyle("-fx-opacity: 1; -fx-background-color: #bcb7b7; -fx-background-radius: 35px; -fx-border-radius: 35px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 5, 0, 5, 5)");

        purchases_quantity.setOnAction(event -> calculateTotalPrice());

        this.purchases_tableCustomers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateSaveButtonState();
        });
        this.purchases_tableItems.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateSaveButtonState();
        });
        this.purchases_date.valueProperty().addListener((obs, oldDate, newDate) -> {
            updateSaveButtonState();
            isDateValidate();
        });
        this.purchases_quantity.textProperty().addListener((obs, oldDate, newDate) -> {
            updateSaveButtonState();
        });
        this.purchases_lblPrice.textProperty().addListener((obs, oldPrice, newPrice) -> {
            updateSaveButtonState();
            quantityText();
        });

        purchases_quantity.setOnMouseClicked(event -> {
            purchases_lblPrice.setText("price");
        });
    }

    private void isDateValidate() {
        LocalDate selectedDate = purchases_date.getValue();
        if (selectedDate != null) {
            LocalDate currentDate = LocalDate.now();

            if (selectedDate.isAfter(currentDate)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Выбранная дата не может быть больше текущей!");
                alert.showAndWait();
                purchases_date.setValue(null);
            }
        }
    }

    private void quantityText() {
        if(purchases_quantity.getText().equals(null) || purchases_quantity.getText().equals("")) {
            purchases_lblPrice.setText("price");
        }
    }

    private void updateSaveButtonState() {
        boolean isCustomerSelected = this.purchases_tableCustomers.getSelectionModel().getSelectedItem() != null;
        boolean isItemSelected = this.purchases_tableItems.getSelectionModel().getSelectedItem() != null;
        boolean isDateSelected = this.purchases_date.getValue() != null;
        boolean isQuantitySelected = this.purchases_quantity.getText() != null && !this.purchases_quantity.getText().isEmpty();
        boolean isPriceDone = !this.purchases_lblPrice.getText().equals("price");

        if (!(isCustomerSelected && isItemSelected && isDateSelected && isQuantitySelected && isPriceDone)) {
            this.purchases_confirmBtn.setDisable(true);
            purchases_confirmBtn.setStyle("-fx-opacity: 1; -fx-background-color: #bcb7b7; -fx-background-radius: 35px; -fx-border-radius: 35px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 5, 0, 5, 5)");
        }
        else {
            this.purchases_confirmBtn.setDisable(false);
            purchases_confirmBtn.setStyle("-fx-opacity: 1; -fx-background-color: #fff; -fx-background-radius: 35px; -fx-border-radius: 35px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 5, 0, 5, 5)");
        }
    }

    private void calculateTotalPrice() {
        try {
            int quantity = Integer.parseInt(purchases_quantity.getText());
            ProductModel prM = (ProductModel)purchases_tableItems.getSelectionModel().getSelectedItem();

            if (prM != null) {
                int availableQuantity = prM.getStockQuantity();

                if (quantity > availableQuantity) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Введённое количество превышает доступное! Укажите значение не больше " + availableQuantity + ".");
                    alert.showAndWait();
                    return;
                }

                double price = prM.getPrice();
                totalPrice = quantity * price;

                purchases_lblPrice.setText(String.format("%.2f", totalPrice));
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Выберите товар!");
                alert.showAndWait();
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Введите корректное число!");
            alert.showAndWait();
        }
    }

    private void loadProducts() {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.GET_ALL_PRODUCTS);

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
                String productsJson = response.getResponseData();
                ProductModel[] productsArray = GSON.fromJson(productsJson, ProductModel[].class);

                ObservableList<ProductModel> products = FXCollections.observableArrayList(productsArray);
                purchases_tableItems.setItems(products);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Ошибка загрузки данных!");
                alert.showAndWait();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                purchases_tableCustomers.setItems(customers);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Ошибка загрузки данных!");
                alert.showAndWait();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void getCustomerFromServer(Customer selectedCustomer) throws IOException {
        Request request = new Request();
        request.setRequestType(RequestType.GET_CUSTOMER);
        String customerJson = GSON.toJson(new Customer(selectedCustomer.getCustomerId()));
        request.setRequestMessage(customerJson);

        ClientSocket.getInstance().getOutStream().println(GSON.toJson(request));
        ClientSocket.getInstance().getOutStream().flush();

        String responseJson = ClientSocket.getInstance().getInStream().readLine();
        if (responseJson == null || responseJson.isEmpty()) {
            throw new RuntimeException("Ответ от сервера пустой");
        }
        Response response = GSON.fromJson(responseJson, Response.class);
        if (response == null) {
            throw new RuntimeException("Ответ от сервера не может быть преобразован в объект Response");
        }

        if (response.getResponseStatus() == ResponseStatus.OK) {
            String customerFromServer = response.getResponseData();
            this.customer = GSON.fromJson(customerFromServer, Customer.class);
        }
    }

    void getProductFromServer(ProductModel selectedProduct) throws IOException {
        Request request = new Request();
        request.setRequestType(RequestType.GET_PRODUCT);
        String productJson = GSON.toJson(new ProductModel(selectedProduct.getProductId()));
        request.setRequestMessage(productJson);

        ClientSocket.getInstance().getOutStream().println(GSON.toJson(request));
        ClientSocket.getInstance().getOutStream().flush();

        String responseJson = ClientSocket.getInstance().getInStream().readLine();
        if (responseJson == null || responseJson.isEmpty()) {
            throw new RuntimeException("Ответ от сервера пустой");
        }
        Response response = GSON.fromJson(responseJson, Response.class);
        if (response == null) {
            throw new RuntimeException("Ответ от сервера не может быть преобразован в объект Response");
        }

        if (response.getResponseStatus() == ResponseStatus.OK) {
            String productFromServer = response.getResponseData();
            this.productModel = GSON.fromJson(productFromServer, ProductModel.class);
        }
    }

    void updateOnStock() {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.UPDATE_PRODUCT);
            String productJson = GSON.toJson(this.productModel);
            request.setRequestMessage(productJson);

            ClientSocket.getInstance().getOutStream().println(GSON.toJson(request));
            ClientSocket.getInstance().getOutStream().flush();

            String responseJson = ClientSocket.getInstance().getInStream().readLine();
            if (responseJson == null || responseJson.isEmpty()) {
                throw new RuntimeException("Ответ от сервера пустой");
            }
            Response response = GSON.fromJson(responseJson, Response.class);
            if (response == null) {
                throw new RuntimeException("Ответ от сервера не может быть преобразован в объект Response");
            }

            if (response.getResponseStatus() == ResponseStatus.OK) {

            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Произошла ошибка при обновлении продукта!");
            alert.showAndWait();
        }
    }

    @FXML
    void ClickOnSave() throws IOException {
        if (purchases_confirmBtn.isDisable()) return;
        else {
            Customer selectedCustomer = (Customer) purchases_tableCustomers.getSelectionModel().getSelectedItem();
            getCustomerFromServer(selectedCustomer);

            ProductModel selectedProduct = (ProductModel) purchases_tableItems.getSelectionModel().getSelectedItem();
            getProductFromServer(selectedProduct);

            int availableQuantity = selectedProduct.getStockQuantity();
            if (Integer.parseInt(purchases_quantity.getText()) > availableQuantity) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Введённое количество превышает доступное! Максимум: " + availableQuantity + ".");
                alert.showAndWait();
                return;
            }

            this.productModel.setStockQuantity(productModel.getStockQuantity() - Integer.parseInt(purchases_quantity.getText()));
            updateOnStock();

            Purchase purchase = new Purchase();
            purchase.setEmployee(ClientSocket.getInstance().getEmployee());

            purchase.setCustomer(customer);
            purchase.setProduct(productModel);
            purchase.setQuantity(Integer.parseInt(purchases_quantity.getText()));
            purchase.setPayAmount(totalPrice);

            LocalDate selectedDate = purchases_date.getValue();

            if (selectedDate != null) {
                LocalDate currentDate = LocalDate.now();

                if (selectedDate.isAfter(currentDate)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Выбранная дата не может быть больше текущей!");
                    alert.showAndWait();
                    return;
                }

                purchase.setDate(selectedDate.toString());
            } else {
                return;
            }

            Request request = new Request();
            request.setRequestType(RequestType.ADD_PURCHASE);
            String purchaseJson = GSON.toJson(purchase);
            request.setRequestMessage(purchaseJson);

            ClientSocket.getInstance().getOutStream().println(GSON.toJson(request));
            ClientSocket.getInstance().getOutStream().flush();

            String responseJson = ClientSocket.getInstance().getInStream().readLine();
            if (responseJson == null || responseJson.isEmpty()) {
                throw new RuntimeException("Ответ от сервера пустой");
            }
            Response response = GSON.fromJson(responseJson, Response.class);
            if (response == null) {
                throw new RuntimeException("Ответ от сервера не может быть преобразован в объект Response");
            }

            if (response.getResponseStatus() == ResponseStatus.OK) {
                loadProducts();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(response.getResponseMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    void showMyPurchases() {
        try {
            Parent pur = FXMLLoader.load(getClass().getResource("/main/resourses/view/employeepurchase.fxml"));
            Scene s = new Scene(pur);
            Stage stg = new Stage();
            stg.setResizable(false);
            stg.setScene(s);
            stg.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
