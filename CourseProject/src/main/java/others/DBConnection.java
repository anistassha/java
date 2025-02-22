package main.java.others;

import javafx.scene.control.Alert;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/course_project?autoReconnect=yes&useSSL=no&serverTimezone=UTC";

    public static Connection getConnection() {
        Connection con;
        try {
            con = DriverManager.getConnection(URL, "root", "password");
        } catch (SQLException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Database connection failed");
            alert.showAndWait();

            /*if (e.getErrorCode() == 0) { //Error Code 0: database server offline
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Database connection failed");
                alert.showAndWait();
            }*/
            return null;
        }
        return con;
    }
}
