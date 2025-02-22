package main.java.controllers;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import main.ClientSocket;
import main.Entities.Employee;
import main.Entities.PersonData;
import main.Enums.RequestType;
import main.Enums.ResponseStatus;
import main.TCP.Request;
import main.TCP.Response;

import java.net.URL;
import java.util.ResourceBundle;

import static main.Enums.RequestType.UPDATE_EMPLOYEE;

public class MyAccountController implements Initializable {

    private static final Gson GSON = new Gson();

    @FXML
    private Button myacc_btnSave;

    @FXML
    private TextField myacc_email;

    @FXML
    private TextField myacc_login;

    @FXML
    private TextField myacc_name;

    @FXML
    private TextField myacc_password;

    @FXML
    private TextField myacc_role;

    @FXML
    private TextField myacc_surname;

    @FXML
    private PasswordField myacc_password_pf;

    @FXML
    private ImageView img_eyeClose;

    @FXML
    private ImageView img_eyeOpen;

    private Employee currentEmployee;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentEmployee = ClientSocket.getInstance().getEmployee();

        if (currentEmployee != null) {
            myacc_login.setText(currentEmployee.getLogin());
            myacc_name.setText(currentEmployee.getPerson().getName());
            myacc_surname.setText(currentEmployee.getPerson().getSurname());
            myacc_email.setText(currentEmployee.getPerson().getEmail());
            myacc_role.setText(currentEmployee.getRole());
        }
    }

    public void myacc_saveAction() {
        try {
            if (myacc_login.getText().isEmpty() ||
                    myacc_name.getText().isEmpty() ||
                    myacc_surname.getText().isEmpty() ||
                    myacc_email.getText().isEmpty() ||
                    myacc_role.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Пожалуйста, заполните все поля перед обновлением профиля!");
                alert.showAndWait();
                return;
            }

            currentEmployee.setLogin(myacc_login.getText());
            currentEmployee.setPassword(currentEmployee.getPassword());
            currentEmployee.setRole(myacc_role.getText());

            PersonData personData = new PersonData();
            personData.setPersonId(ClientSocket.getInstance().getEmployee().getPerson().getPersonId());
            personData.setName(myacc_name.getText().trim());
            personData.setSurname(myacc_surname.getText().trim());
            personData.setEmail(myacc_email.getText().trim());

            currentEmployee.setPerson(personData);

            Request request = new Request();
            request.setRequestType(UPDATE_EMPLOYEE);
            String employeeJson = GSON.toJson(currentEmployee);
            request.setRequestMessage(employeeJson);

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
                alert.setContentText("Профиль успешно обновлен!");
                alert.showAndWait();

                ClientSocket.getInstance().setEmployee(currentEmployee);
                ClientSocket.getInstance().getEmployee().setLogin(myacc_login.getText());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Ошибка обновления профиля!");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Произошла ошибка при обновлении профиля!");
            alert.showAndWait();
        }
    }

}

