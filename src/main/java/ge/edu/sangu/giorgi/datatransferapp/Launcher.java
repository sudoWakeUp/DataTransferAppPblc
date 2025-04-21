package ge.edu.sangu.giorgi.datatransferapp;

import ge.edu.sangu.giorgi.datatransferapp.scenes.AuthScene;
import ge.edu.sangu.giorgi.datatransferapp.scenes.LoginScene;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Launcher extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //LoginScene.getScene().setLoginScene(stage);
        AuthScene.getScene().setLoginScene(stage);
    }

    public static void main(String[] args) {
        DBConnect.connect();

        launch();
    }
}