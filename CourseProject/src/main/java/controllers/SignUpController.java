package main.java.controllers;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.ClientSocket;
import main.Entities.Employee;
import main.Entities.PersonData;
import main.Enums.RequestType;
import main.Enums.ResponseStatus;
import main.TCP.Request;
import main.TCP.Response;
import main.java.others.PasswordUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class SignUpController {
    private static final Gson GSON = new Gson();

    @FXML
    private Label button_goToLogin;

    @FXML
    private Button button_reg;

    @FXML
    private TextField reg_email;

    @FXML
    private ImageView reg_img_eyeClose;

    @FXML
    private ImageView reg_img_eyeOpen;

    @FXML
    private TextField reg_login;

    @FXML
    private TextField reg_name;

    @FXML
    private PasswordField reg_password;

    @FXML
    private TextField reg_password1;

    @FXML
    private TextField reg_surname;

//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        reg_password.setVisible(true);
//        reg_password1.setVisible(false);
//        reg_img_eyeOpen.setVisible(false);
//        reg_img_eyeClose.setVisible(true);
//    }

    @FXML
    void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resourses/view/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) button_reg.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void SignUpPressed(ActionEvent event) throws IOException {
        Employee employee = new Employee();
        employee.setLogin(reg_login.getText().trim());

        String hashedPassword = PasswordUtils.hashPassword(reg_password.getText().trim());
        employee.setPassword(hashedPassword);
  //      employee.setPassword(reg_password.getText().trim());
        employee.setRole("Сотрудник");

        PersonData personData = new PersonData();
        personData.setName(reg_name.getText().trim());
        personData.setSurname(reg_surname.getText().trim());
        personData.setEmail(reg_email.getText().trim());

        employee.setPerson(personData);

        if (reg_login.getText().trim().isEmpty() || reg_password.getText().trim().isEmpty() ||
                reg_name.getText().trim().isEmpty() || reg_surname.getText().trim().isEmpty() ||
                reg_email.getText().trim().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Пожалуйста, заполните все поля!");
            alert.showAndWait();
            return;
        }

      /*  String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        String regex = "^[A-Za-z0-9_-]{4,50}$"; // Регулярное выражение для проверки
        Pattern pattern = Pattern.compile(regex);

        if (!pattern.matcher(login).matches()) {
            labelMessage.setText("Логин может содержать английские буквы, цифры, -, _ и >4 символов");
            labelMessage.setVisible(true);
            return;
        }
        if (!pattern.matcher(password).matches()) {
            labelMessage.setText("Пароль может содержать английские буквы, цифры, -, _ и >4 символов");
            labelMessage.setVisible(true);
            return;
        }*/

        Request request = new Request();
        request.setRequestMessage(new Gson().toJson(employee));
        request.setRequestType(RequestType.REGISTER);
        System.out.println("Request sent to server: " + request.getRequestMessage());

        ClientSocket.getInstance().getOutStream().println(new Gson().toJson(request));
        ClientSocket.getInstance().getOutStream().flush();
        String answer = ClientSocket.getInstance().getInStream().readLine();
        System.out.println("Response from server: " + answer);

        Response response = new Gson().fromJson(answer, Response.class);
        if (response.getResponseStatus() == ResponseStatus.OK) {
            String employeeJson = response.getResponseData();
            Employee authenticatedEmployee = GSON.fromJson(employeeJson, Employee.class);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Регистрация пройдена успешно!");
            alert.showAndWait();

            ClientSocket.getInstance().setEmployee(authenticatedEmployee);

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main/resourses/view/main_menu.fxml"));
            Parent root = fxmlLoader.load();

            MainMenuController mainMenuController = fxmlLoader.getController();
            mainMenuController.setEmployeeData(authenticatedEmployee);

            Stage stage = (Stage) button_reg.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Пользователь с таким логином уже существует!");
            alert.showAndWait();
        }
    }

//    @FXML
//    private void togglePasswordVisibility() {
//        if (reg_password.isVisible()) {
//            reg_password.setVisible(false);
//            reg_password1.setVisible(true);
//            reg_password1.setText(reg_password.getText());
//            reg_img_eyeOpen.setVisible(true);
//            reg_img_eyeClose.setVisible(false);
//        } else {
//            reg_password.setVisible(true);
//            reg_password1.setVisible(false);
//            reg_password.setText(reg_password1.getText());
//            reg_img_eyeOpen.setVisible(false);
//            reg_img_eyeClose.setVisible(true);
//        }
//    }

}
