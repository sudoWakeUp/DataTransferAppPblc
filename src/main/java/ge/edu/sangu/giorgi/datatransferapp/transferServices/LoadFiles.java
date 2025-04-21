package ge.edu.sangu.giorgi.datatransferapp.transferServices;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import ge.edu.sangu.giorgi.datatransferapp.alerts.Alert;
import ge.edu.sangu.giorgi.datatransferapp.alerts.Error;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.Vector;

public class LoadFiles {

    public void load(
            TableView<ChannelSftp.LsEntry> tableView,
            TextField pathField,
            ChannelSftp sftpChannel
            )
    {
        String directory = pathField.getText();
        Thread t = new Thread(() -> {
            try {
                Vector<ChannelSftp.LsEntry> fileList = sftpChannel.ls(directory);
                List<ChannelSftp.LsEntry> filteredList = fileList.stream()
                        .filter(entry -> !entry.getFilename().equals(".") && !entry.getFilename().equals(".."))
                        .toList();

                ObservableList<ChannelSftp.LsEntry> observableList = FXCollections.observableArrayList(filteredList);
                Platform.runLater(() -> {
                    tableView.setItems(observableList);
                    pathField.setText(directory);
                });
            } catch (SftpException e) {
                Platform.runLater(() -> new Alert(new Error()).show("Failed to list Files"));
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            new Alert(new Error()).show(e.getMessage());
        }
    }
}
