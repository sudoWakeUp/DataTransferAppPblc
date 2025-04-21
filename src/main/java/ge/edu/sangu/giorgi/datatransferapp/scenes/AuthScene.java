package ge.edu.sangu.giorgi.datatransferapp.scenes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class AuthScene {

    private static final AuthScene instance = new AuthScene();

    private AuthScene(){};

    public static AuthScene getScene() {
        return instance;
    }

    public void setLoginScene(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ge/edu/sangu/giorgi/datatransferapp/register.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 500);
        scene.getRoot().requestFocus();

        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                "/ge/edu/sangu/giorgi/datatransferapp/images/data-transfer.png"))));

        stage.setTitle("Transfer And Chill");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(
                "/ge/edu/sangu/giorgi/datatransferapp/css/login.css")).toExternalForm());
    }
}
