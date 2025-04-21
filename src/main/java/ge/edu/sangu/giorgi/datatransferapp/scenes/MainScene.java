package ge.edu.sangu.giorgi.datatransferapp.scenes;

import com.jcraft.jsch.Session;
import ge.edu.sangu.giorgi.datatransferapp.controllers.FilesController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainScene {
    private static final MainScene instance = new MainScene();

    private MainScene(){};

    public static MainScene getInstance(){
        return instance;
    }

    public void setMainScene(Session session, Label messageLabel){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ge/edu/sangu/giorgi/datatransferapp/mainView.fxml"));
            Parent root = loader.load();
            Platform.runLater(root::requestFocus);
            root.setOnMouseClicked(event -> root.requestFocus());

            FilesController filesController = loader.getController();
            filesController.initialize(session);

            Stage stage = (Stage) messageLabel.getScene().getWindow();
            Scene scene = new Scene(root, 700, 500);
            stage.setScene(scene);
            stage.show();

            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(
                    "/ge/edu/sangu/giorgi/datatransferapp/css/mainView.css")).toExternalForm());

        } catch (IOException e) {
            messageLabel.setText("Failed to load main view: " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }
}
