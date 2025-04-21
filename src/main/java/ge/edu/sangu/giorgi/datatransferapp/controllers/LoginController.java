package ge.edu.sangu.giorgi.datatransferapp.controllers;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import ge.edu.sangu.giorgi.datatransferapp.Clock;
import ge.edu.sangu.giorgi.datatransferapp.alerts.Alert;
import ge.edu.sangu.giorgi.datatransferapp.alerts.Error;
import ge.edu.sangu.giorgi.datatransferapp.scenes.AuthScene;
import ge.edu.sangu.giorgi.datatransferapp.scenes.MainScene;
import ge.edu.sangu.giorgi.datatransferapp.usersServices.AuthData;
import ge.edu.sangu.giorgi.datatransferapp.usersServices.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class LoginController {
    @FXML private TextField addressField;
    @FXML private TextField portField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML private Label loggedAsLabel;
    @FXML private Label logoutLabel;
    @FXML private Label clock;

    public void initialize(){
        List<User> users = AuthData.getUserData();
        if(users != null && !users.isEmpty()){
            loggedAsLabel.setText(users.getFirst().getFullName());
        }
        Clock.getClock(clock);
    }

    @FXML
    protected void onLoginButtonClick() {
        String address = addressField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        int port = Integer.parseInt(portField.getText());

        Platform.runLater(() -> messageLabel.setText("Connecting to the server..."));

        new Thread(() -> {
            JSch jsch = new JSch();
            try {
                Session session = jsch.getSession(username, address, port);
                session.setPassword(password);
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();

                Platform.runLater(() -> {
                    messageLabel.setText("Connected to " + username + "@" + address + ":" + port);
                    MainScene.getInstance().setMainScene(session, messageLabel);
                });

            } catch (JSchException e) {
                Platform.runLater(() -> {
                    messageLabel.setText("Try again");
                    new Alert(new Error()).show("Error: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML private void onLogoutLabelClick(){
        try {
            AuthScene.getScene().setLoginScene((Stage) logoutLabel.getScene().getWindow());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}