package ge.edu.sangu.giorgi.datatransferapp.scenes;

import ge.edu.sangu.giorgi.datatransferapp.controllers.RenamePopUpController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class RenamePopUpScene {
    private static final RenamePopUpScene instance = new RenamePopUpScene();

    private RenamePopUpScene(){};

    public static RenamePopUpScene getScene() {
        return instance;
    }

    public String setRenamePopUpScene(String fileName) throws IOException {
        FXMLLoader renamePopUp = new FXMLLoader(getClass().getResource("/ge/edu/sangu/giorgi/datatransferapp/renamePopUp.fxml"));

        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Rename");
        Scene scene = new Scene(renamePopUp.load());

        RenamePopUpController controller = renamePopUp.getController();
        controller.setFilename(fileName);

        scene.getRoot().requestFocus();
        stage.setScene(scene);
        stage.showAndWait();

        return controller.getNewName();
    }
}
