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
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.ClientSocket;
import main.Entities.Customer;
import main.Entities.ProductModel;
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
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomersController implements Initializable {

    @FXML
    private Button customers_backBtn;

    @FXML
    private Button customers_btnAdd;

    @FXML
    private ImageView customers_image_backBtn;

    @FXML
    private ImageView customers_image_addBtn;

    @FXML
    private ImageView customers_searchImg;

    @FXML
    private ImageView customers_krestImg;

    @FXML
    private Button customers_btnAllCustomers;

    @FXML
    private Button customers_btnDelete;

    @FXML
    private Button customers_btnLeft;

    @FXML
    private Button customers_btnRight;

    @FXML
    private Button customers_btnRurchases;

    @FXML
    private Button customers_btnSave;

    @FXML
    private Button customers_btnSearch;

    @FXML
    private TextField customers_customerEmail;

    @FXML
    private TextField customers_customerID;

    @FXML
    private TextField customers_customerName;

    @FXML
    private TextField customers_customerNumber;

    @FXML
    private RadioButton customers_customerSexM;

    @FXML
    private RadioButton customers_customerSexW;

    @FXML
    private TextField customers_customerSurname;

    @FXML
    private Label customers_labelShowOf;

    @FXML
    private Label customers_searchLbl;

    @FXML
    private TextField customers_search;

    private static final Gson GSON = new Gson();

    private List<Customer> customersList;
    private int currentIndex = 0;
    private static boolean addFlag = false;
    private static boolean searchDone = false;

    private void updateCustomerLabel() {
        int totalCustomers = customersList.size();
        int currentCustomerNumber = currentIndex + 1;
        customers_labelShowOf.setText("Показан " + currentCustomerNumber + " из " + totalCustomers + " покупателей");
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

                customersList = FXCollections.observableArrayList(customersArray);
                currentIndex = 0;
                setCustomerFields(customersList.get(currentIndex));
                updateCustomerLabel();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Ошибка загрузки данных!");
                alert.showAndWait();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setCustomerFields(Customer customer) {
        customers_customerID.setText(String.valueOf(customer.getCustomerId()));
        customers_customerName.setText(customer.getFirstName());
        customers_customerSurname.setText(customer.getLastName());
        customers_customerEmail.setText(customer.getEmail());
        customers_customerNumber.setText(customer.getPhoneNumber());

        if ("Мужской".equals(customer.getGender())) {
            customers_customerSexM.setSelected(true);
            customers_customerSexW.setSelected(false);
        } else {
            customers_customerSexW.setSelected(true);
            customers_customerSexM.setSelected(false);
        }

        updateCustomerLabel();
    }

    @FXML
    void goToNextCustomer() {
        if (currentIndex < customersList.size() - 1) {
            currentIndex++;
            setCustomerFields(customersList.get(currentIndex));
        }

        if (currentIndex == customersList.size() - 1) {
            customers_btnRight.setDisable(true);
            customers_btnRight.setStyle("-fx-opacity: 0.5; -fx-background-color: #fff; -fx-background-radius: 35px; -fx-border-radius: 35px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 5, 0, 5, 5)");
        } else {
            customers_btnRight.setDisable(false);
            customers_btnRight.setStyle("-fx-opacity: 1; -fx-background-color: #fff; -fx-background-radius: 35px; -fx-border-radius: 35px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 5, 0, 5, 5)");
        }

        customers_btnLeft.setDisable(false);
        customers_btnLeft.setStyle("-fx-opacity: 1; -fx-background-color: #fff; -fx-background-radius: 35px; -fx-border-radius: 35px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 5, 0, 5, 5)");

        updateCustomerLabel();
    }

    @FXML
    void goToPreviousCustomer() {
        if (currentIndex > 0) {
            currentIndex--;
            setCustomerFields(customersList.get(currentIndex));
        }

        if (currentIndex == 0) {
            customers_btnLeft.setDisable(true);
            customers_btnLeft.setStyle("-fx-opacity: 0.5; -fx-background-color: #fff; -fx-background-radius: 35px; -fx-border-radius: 35px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 5, 0, 5, 5)");
        } else {
            customers_btnLeft.setDisable(false);
            customers_btnLeft.setStyle("-fx-opacity: 1; -fx-background-color: #fff; -fx-background-radius: 35px; -fx-border-radius: 35px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 5, 0, 5, 5)");
        }

        customers_btnRight.setDisable(false);
        customers_btnRight.setStyle("-fx-opacity: 1; -fx-background-color: #fff; -fx-background-radius: 35px; -fx-border-radius: 35px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 5, 0, 5, 5)");

        updateCustomerLabel();
    }

    @FXML
    void showAllCustomers() {
        try {
            Scene scene = customers_btnAllCustomers.getScene();
            AnchorPane paneRight = (AnchorPane) scene.lookup("#paneRight");

            if (paneRight != null) {
                paneRight.getChildren().clear();

                AnchorPane newPane = FXMLLoader.load(getClass().getResource("/main/resourses/view/list_all_customers.fxml"));
                AnchorPane.setTopAnchor(newPane, 0.0);
                AnchorPane.setRightAnchor(newPane, 0.0);
                AnchorPane.setBottomAnchor(newPane, 0.0);
                AnchorPane.setLeftAnchor(newPane, 0.0);

                paneRight.getChildren().add(newPane);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void addNewCustomer() {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.GET_LAST_CUSTOMER_ID);

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
                int lastCustomerId = Integer.parseInt(response.getResponseData());
                customers_customerID.setText(String.valueOf(lastCustomerId + 1));
            }

            customers_customerName.clear();
            customers_labelShowOf.setVisible(false);
            customers_customerSurname.clear();
            customers_customerEmail.clear();
            customers_customerNumber.clear();
            customers_customerSexM.setSelected(false);
            customers_customerSexW.setSelected(false);
            customers_btnAdd.setVisible(false);
            customers_image_addBtn.setVisible(false);
            customers_backBtn.setVisible(true);
            customers_image_backBtn.setVisible(true);
            customers_btnRight.setDisable(true);
            customers_btnRight.setStyle("-fx-opacity: 0.5; -fx-background-color: #fff; -fx-background-radius: 35px; -fx-border-radius: 35px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 5, 0, 5, 5)");
            customers_btnLeft.setDisable(true);
            customers_btnLeft.setStyle("-fx-opacity: 0.5; -fx-background-color: #fff; -fx-background-radius: 35px; -fx-border-radius: 35px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 5, 0, 5, 5)");

            addFlag = true;
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Ошибка соединения с сервером!");
            alert.showAndWait();
        }
    }

    @FXML
    void customers_turnBack() {
        loadCustomers();
        goToPreviousCustomer();
        customers_btnAdd.setVisible(true);
        customers_image_addBtn.setVisible(true);
        customers_backBtn.setVisible(false);
        customers_image_backBtn.setVisible(false);
        customers_labelShowOf.setVisible(true);
        addFlag = false;
    }

    boolean checkFields() {
        boolean flag;
        if (customers_customerID.getText().isEmpty() ||
                customers_customerName.getText().isEmpty() ||
                customers_customerSurname.getText().isEmpty() ||
                customers_customerEmail.getText().isEmpty() ||
                customers_customerNumber.getText().isEmpty() ||
                (!customers_customerSexM.isSelected() && !customers_customerSexW.isSelected())) flag = false; else flag = true;
        return flag;
    }

    @FXML
    void customers_saveAction() {
        if (addFlag) {
            if (!checkFields()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Пожалуйста, заполните все поля перед добавлением покупателя!");
                alert.showAndWait();
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтвердите действие");

            alert.setHeaderText("Вы действительно хотите добавить покупателя?");
            alert.setContentText("Нажмите OK для подтверждения");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {
                addRecordToDatabase();
                customers_image_addBtn.setVisible(true);
                customers_image_backBtn.setVisible(false);

                customers_btnAdd.setVisible(true);
                customers_backBtn.setVisible(false);
            }
        } else {
            if (!checkFields()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Пожалуйста, заполните все поля для обновления данных покупателя!");
                alert.showAndWait();
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтвердите действие");

            alert.setHeaderText("Вы действительно хотите обновить данные покупателя?");
            alert.setContentText("Нажмите OK для подтверждения");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {
                updateRecord();
            }
        }
    }

    void addRecordToDatabase() {
        try {
            int customerId = Integer.parseInt(customers_customerID.getText());
            String firstName = customers_customerName.getText();
            String lastName = customers_customerSurname.getText();
            String email = customers_customerEmail.getText();
            String phoneNumber = customers_customerNumber.getText();
            String gender = customers_customerSexM.isSelected() ? "Мужской" : "Женский";

            Customer newCustomer = new Customer(customerId, firstName, lastName, gender, phoneNumber, email);

            Request request = new Request();
            request.setRequestType(RequestType.ADD_CUSTOMER);
            String customerJson = GSON.toJson(newCustomer);
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
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Покупатель успешно добавлен!");
                alert.showAndWait();
                //loadCustomers();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Ошибка добавления покупателя!");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Произошла ошибка при добавлении покупателя!");
            alert.showAndWait();
        }
    }

    void updateRecord() {
        try {
            int customerId = Integer.parseInt(customers_customerID.getText());
            String firstName = customers_customerName.getText();
            String lastName = customers_customerSurname.getText();
            String email = customers_customerEmail.getText();
            String phoneNumber = customers_customerNumber.getText();
            String gender = customers_customerSexM.isSelected() ? "Мужской" : "Женский";

            Customer updateCustomer = new Customer(customerId, firstName, lastName, gender, phoneNumber, email);

            Request request = new Request();
            request.setRequestType(RequestType.UPDATE_CUSTOMER);
            String customerJson = GSON.toJson(updateCustomer);
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
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Данные покупателя успешно обновлены!");
                alert.showAndWait();
                //loadCustomers();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Ошибка обновления данных покупателя!");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Произошла ошибка при обновлении данных покупателя!");
            alert.showAndWait();
        }
    }

    @FXML
    void deleteCustomer() {
        try {
            int customerId = Integer.parseInt(customers_customerID.getText());
            String firstName = customers_customerName.getText();
            String lastName = customers_customerSurname.getText();
            String email = customers_customerEmail.getText();
            String phoneNumber = customers_customerNumber.getText();
            String gender = customers_customerSexM.isSelected() ? "Мужской" : "Женский";

            Customer customer = new Customer(customerId, firstName, lastName, gender, phoneNumber, email);

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Подтверждение удаления");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Вы уверены, что хотите удалить покупателя с ID \"" + customer.getCustomerId() + "\"?");
            if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                return;
            }

            Request request = new Request();
            request.setRequestType(RequestType.DELETE_CUSTOMER);
            String customerJson = GSON.toJson(customer);
            request.setRequestMessage(customerJson);

            ClientSocket.getInstance().getOutStream().println(GSON.toJson(request));
            ClientSocket.getInstance().getOutStream().flush();

            String responseJson = ClientSocket.getInstance().getInStream().readLine();
            if (responseJson == null || responseJson.isEmpty()) {
                throw new RuntimeException("Ответ от сервера пустой");
            }

            Response response = GSON.fromJson(responseJson, Response.class);
            if (response.getResponseStatus() == ResponseStatus.OK) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Покупатель успешно удален!");
                alert.showAndWait();
                loadCustomers();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Ошибка удаления покупателя!");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Произошла ошибка при удалении покупателя!");
            alert.showAndWait();
        }
    }

    private ObservableList<Customer> searchWithID(Integer id) {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        try {
            Request request = new Request();
            request.setRequestType(RequestType.SEARCH_CUSTOMERS_ID);
            request.setRequestMessage(String.valueOf(id));

            ClientSocket.getInstance().getOutStream().println(GSON.toJson(request));
            ClientSocket.getInstance().getOutStream().flush();

            String responseJson = ClientSocket.getInstance().getInStream().readLine();
            if (responseJson == null || responseJson.isEmpty()) {
                throw new RuntimeException("Ответ от сервера пустой");
            }

            Response response = GSON.fromJson(responseJson, Response.class);
            if (response.getResponseStatus() == ResponseStatus.OK) {
                String customersJson = response.getResponseData();
                Customer[] customersArray = GSON.fromJson(customersJson, Customer[].class);

                customers = FXCollections.observableArrayList(customersArray);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Ошибка при поиске покупателей!");
            alert.showAndWait();
        }
        return customers;
    }

    private ObservableList<Customer> searchWithSurname(String surname) {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        try {
            Request request = new Request();
            request.setRequestType(RequestType.SEARCH_CUSTOMERS_SURNAME);
            request.setRequestMessage(surname);

            ClientSocket.getInstance().getOutStream().println(GSON.toJson(request));
            ClientSocket.getInstance().getOutStream().flush();

            String responseJson = ClientSocket.getInstance().getInStream().readLine();
            if (responseJson == null || responseJson.isEmpty()) {
                throw new RuntimeException("Ответ от сервера пустой");
            }

            Response response = GSON.fromJson(responseJson, Response.class);
            if (response.getResponseStatus() == ResponseStatus.OK) {
                String customersJson = response.getResponseData();
                Customer[] customersArray = GSON.fromJson(customersJson, Customer[].class);

                customers = FXCollections.observableArrayList(customersArray);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Ошибка при поиске покупателей!");
            alert.showAndWait();
        }
        return customers;
    }

    @FXML
    void customers_btnSearchAction() {
        if (searchDone) {
            searchDone = false;
            customers_searchLbl.setVisible(false);
            customers_searchImg.setVisible(true);
            customers_krestImg.setVisible(false);
            loadCustomers();
        } else {
            ObservableList<Customer> searchResult = FXCollections.observableArrayList();
            try {
                Integer id = Integer.valueOf(customers_search.getText());
                searchResult = searchWithID(id);
            } catch (NumberFormatException e) {
                String surname = customers_search.getText();
                searchResult = searchWithSurname(surname);
            } finally {
                if (searchResult.size() <= 0) {
                    customers_searchLbl.setText("Соответствий не найдено!");
                    customers_searchLbl.setVisible(true);
                } else {
                   // customersList = FXCollections.observableArrayList(searchResult);
                    customersList = searchResult;
                    currentIndex = 0;
                    setCustomerFields(customersList.get(currentIndex));
                    updateCustomerLabel();
                    searchDone = true;
                    customers_searchImg.setVisible(false);
                    customers_krestImg.setVisible(true);
                    customers_searchLbl.setVisible(true);
                    int recordSize = searchResult.size();
                    customers_searchLbl.setText("Найдено " + recordSize + "!");
                   // customersList = searchResult;
                }
            }
        }
    }

    @FXML
    void showPurchases() {
        try {
            CustomerPurchaseController.customerID = Integer.valueOf(customers_customerID.getText());
            Parent pur = FXMLLoader.load(getClass().getResource("/main/resourses/view/customerpurchase.fxml"));
            Scene s = new Scene(pur);
            Stage stg = new Stage();
            stg.setResizable(false);
            stg.setScene(s);
            stg.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadCustomers();
        goToPreviousCustomer();
    }
}


