package ge.edu.sangu.giorgi.datatransferapp.controllers;

import ge.edu.sangu.giorgi.datatransferapp.Clock;
import ge.edu.sangu.giorgi.datatransferapp.alerts.Alert;
import ge.edu.sangu.giorgi.datatransferapp.alerts.Error;
import ge.edu.sangu.giorgi.datatransferapp.scenes.LoginScene;
import ge.edu.sangu.giorgi.datatransferapp.usersServices.Authentication;
import ge.edu.sangu.giorgi.datatransferapp.usersServices.Registration;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class AuthController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private Label authLabel;
    @FXML private Label clock;
    @FXML private Button authButton;
    @FXML private GridPane gridPane;
    @FXML private StackPane stackPane;

    public void initialize(){
        nameField.setManaged(false);
        clock.setViewOrder(1);
        Clock.getClock(clock);

    }

    @FXML private void onUserLogin(){
        String email = emailField.getText();
        String password = passwordField.getText();
        Stage stage = (Stage) passwordField.getScene().getWindow();

        Map<String, String> response = Authentication.login(email, password);

        if(Objects.equals(response.get("statusCode"), "200")){
            try {
                LoginScene.getScene().setLoginScene(stage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            new Alert(new Error()).show("Username or password is false");
        }
    }

    @FXML private void onAuthLabelClick(){
        String authLabelText = authLabel.getText();

        if(authLabelText.equals("Register")){
            changeToRegister();
        } else if (authLabelText.equals("Login")) {
            changeToLogin();
        }
    }

    @FXML private void onGuestLabelClick(){
        Map<String, String> response = Authentication.login("guestemail@email.com", "0000");

        if(Objects.equals(response.get("statusCode"), "200")){
            try {
                LoginScene.getScene().setLoginScene((Stage) passwordField.getScene().getWindow());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            new Alert(new Error()).show("Username or password is false");
        }
    }

    private void onRegisterClick(){
        boolean userRegistered = Registration.register(nameField.getText(), emailField.getText(), passwordField.getText());
        if(userRegistered)
            changeToLogin();
    }

    private void changeToLogin(){
        authLabel.setText("Register");
        authButton.setText("Login");
        authButton.setOnAction(e -> onUserLogin());
        emailField.setText("");
        passwordField.setText("");
        nameField.setVisible(false);
        nameField.setManaged(false);
        gridPane.requestFocus();
    }

    private void changeToRegister(){
        authLabel.setText("Login");
        authButton.setText("Register");
        authButton.setOnAction(e -> onRegisterClick());
        emailField.setText("");
        passwordField.setText("");
        nameField.setVisible(true);
        nameField.setManaged(true);
        nameField.setText("");
        gridPane.requestFocus();
    }
}
