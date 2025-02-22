package main.java.controllers;

import com.google.gson.Gson;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import main.ClientSocket;
import main.Entities.Employee;
import main.Entities.ProductModel;
import main.Enums.RequestType;
import main.Enums.ResponseStatus;
import main.TCP.Request;
import main.TCP.Response;

import javax.management.relation.Role;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EmployeeController implements Initializable {

    private static final Gson GSON = new Gson();

    @FXML
    private TableColumn<Employee, Number> emp_col_id;

    @FXML
    private TableColumn<Employee, String> emp_col_name;

    @FXML
    private TableColumn<Employee, String> emp_col_role;

    @FXML
    private TableColumn<Employee, Number> emp_col_status;

    @FXML
    private TableColumn<Employee, String> emp_col_surname;

    @FXML
    private Button emp_roleBtn;

    @FXML
    private Button emp_statusBtn;

    @FXML
    private TableView<Employee> employees_table;

    private Employee selectedEmployee;
    private Employee currentEmployee = ClientSocket.getInstance().getEmployee();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        emp_col_id.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getEmployeeId()));

        emp_col_name.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPerson().getName()));

        emp_col_surname.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPerson().getSurname()));

        emp_col_role.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRole()));

//        emp_col_status.setCellValueFactory(cellData ->
//                new SimpleObjectProperty<>(cellData.getValue().getStatus()));

        emp_col_status.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getStatus()));

        emp_col_status.setCellFactory(column -> {
            return new TableCell<Employee, Number>() {
                @Override
                protected void updateItem(Number item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.intValue() == 0 ? "Активен" : "Заблокирован");
                    }
                }
            };
        });

        loadEmployees();

        employees_table.setOnMouseClicked(event -> {
            selectedEmployee = employees_table.getSelectionModel().getSelectedItem();
        });

        employees_table.setRowFactory(tv -> {
            TableRow<Employee> row = new TableRow<>();
            row.setStyle("");

            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && newItem.getEmployeeId() == currentEmployee.getEmployeeId()) {
                    row.setStyle("-fx-background-color: #90EE90;");
                } else {
                    row.setStyle("");
                }
            });

            return row;
        });
    }

    private void loadEmployees() {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.GET_ALL_EMPLOYEES);

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
                String employeesJson = response.getResponseData();
                Employee[] employeesArray = GSON.fromJson(employeesJson, Employee[].class);

                ObservableList<Employee> employees = FXCollections.observableArrayList(employeesArray);
                employees_table.setItems(employees);
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
    void changeRole(ActionEvent event) {
        if (selectedEmployee == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Выберите сотрудника для изменения роли.");
            alert.showAndWait();
            return;
        }

        if (selectedEmployee.getEmployeeId() == currentEmployee.getEmployeeId()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Нельзя изменить собственную роль.");
            alert.showAndWait();
            return;
        }

        String newRole = selectedEmployee.getRole().equals("Сотрудник") ? "Администратор" : "Сотрудник";
        selectedEmployee.setRole(newRole);

        try {
            Request request = new Request();
            request.setRequestType(RequestType.UPDATE_EMPLOYEE);
            request.setRequestMessage(GSON.toJson(selectedEmployee));

            ClientSocket.getInstance().getOutStream().println(GSON.toJson(request));
            ClientSocket.getInstance().getOutStream().flush();

            String answer = ClientSocket.getInstance().getInStream().readLine();
            if (answer == null || answer.isEmpty()) {
                throw new RuntimeException("Ответ от сервера пустой");
            }

            Response response = GSON.fromJson(answer, Response.class);
            if (response != null && response.getResponseStatus() == ResponseStatus.OK) {
                loadEmployees();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Роль сотрудника успешно изменена.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Ошибка при изменении роли.");
                alert.showAndWait();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void changeStatus(ActionEvent event) {
        if (selectedEmployee == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Выберите сотрудника для изменения статуса.");
            alert.showAndWait();
            return;
        }

        if (selectedEmployee.getEmployeeId() == currentEmployee.getEmployeeId()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Нельзя изменить собственный статус.");
            alert.showAndWait();
            return;
        }

        int newStatus = selectedEmployee.getStatus() == 0 ? 1 : 0;
        selectedEmployee.setStatus(newStatus);

        try {
            Request request = new Request();
            request.setRequestType(RequestType.UPDATE_EMPLOYEE);
            request.setRequestMessage(GSON.toJson(selectedEmployee));

            ClientSocket.getInstance().getOutStream().println(GSON.toJson(request));
            ClientSocket.getInstance().getOutStream().flush();

            String answer = ClientSocket.getInstance().getInStream().readLine();
            if (answer == null || answer.isEmpty()) {
                throw new RuntimeException("Ответ от сервера пустой");
            }

            Response response = GSON.fromJson(answer, Response.class);
            if (response != null && response.getResponseStatus() == ResponseStatus.OK) {
                loadEmployees();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Статус сотрудника успешно изменен.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Ошибка при изменении статуса.");
                alert.showAndWait();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
