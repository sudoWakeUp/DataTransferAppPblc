package ge.edu.sangu.giorgi.datatransferapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RenamePopUpController {
    @FXML private Button cancelButton;
    @FXML private Button renameButton;
    @FXML private TextField newNameField;
    private String newName;

    public void setFilename(String filename) {
        newNameField.setText(filename);
    }

    public String getNewName() {
        return newName;
    }

    @FXML
    private void onCancelButtonClick() {
        newName = null;
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    @FXML
    private void onRenameButtonClick() {
        if (!newNameField.getText().trim().isBlank()) {
            newName = newNameField.getText().trim();
            ((Stage) renameButton.getScene().getWindow()).close();
        }
    }
}
