package ge.edu.sangu.giorgi.datatransferapp.alerts;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class Error implements Notification{
    @Override
    public void showAlert(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource(
                "/ge/edu/sangu/giorgi/datatransferapp/css/alert.css")).toExternalForm());

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(
                new Image(Objects.requireNonNull(Info.class.getResourceAsStream(
                        "/ge/edu/sangu/giorgi/datatransferapp/images/error.png"
                ))
                ));

        alert.setGraphic(null);

        alert.showAndWait();
    }
}
