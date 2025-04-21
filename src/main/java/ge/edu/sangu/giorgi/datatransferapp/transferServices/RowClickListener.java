package ge.edu.sangu.giorgi.datatransferapp.transferServices;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import ge.edu.sangu.giorgi.datatransferapp.alerts.Alert;
import ge.edu.sangu.giorgi.datatransferapp.alerts.Error;
import ge.edu.sangu.giorgi.datatransferapp.scenes.RenamePopUpScene;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Deque;
import java.util.Vector;

public class RowClickListener {

    public interface DirectoryChangeCallback {
        void onDirectoryChanged(String newPath, String previousPath);
    }

    public void setupRowClickListener(
            TableView<ChannelSftp.LsEntry> tableView,
            TextField pathField,
            ChannelSftp sftpChannel,
            Deque<String> historyStack,
            DirectoryChangeCallback callback,
            Runnable updateBackButton
    ) {
        tableView.getSelectionModel().selectionModeProperty().set(SelectionMode.MULTIPLE);
        tableView.setRowFactory(tv -> {
            TableRow<ChannelSftp.LsEntry> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem rename = new MenuItem("Rename");
            MenuItem delete = new MenuItem("Delete");

            contextMenu.getItems().addAll(rename, delete);
            row.setContextMenu(contextMenu);

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    ChannelSftp.LsEntry selectedEntry = row.getItem();
                    if (selectedEntry.getAttrs().isDir()) {
                        String newPath = pathField.getText().endsWith("/")
                                ? pathField.getText() + selectedEntry.getFilename()
                                : pathField.getText() + "/" + selectedEntry.getFilename();

                        historyStack.push(pathField.getText());
                        callback.onDirectoryChanged(newPath, pathField.getText());
                        updateBackButton.run();
                    }
                }
            });

            delete.setOnAction(event -> {
                String path = pathField.getText()+"/"+ row.getItem().getFilename();
                if (!row.isEmpty() && row.getItem() != null) {
                    System.out.println("Clicked on delete: " + row.getItem().getFilename());
                    try {
                        if(sftpChannel.lstat(path).isDir()){
                            deleteDirectoryRecursive(sftpChannel, path);
                        } else {
                            sftpChannel.rm(pathField.getText() + "/" + row.getItem().getFilename());
                        }
                        new LoadFiles().load(tableView, pathField, sftpChannel);
                    } catch (SftpException e) {
                        System.out.println("Can't remove file");
                        e.printStackTrace();
                    }
                }
            });

            rename.setOnAction(event -> {
                if (!row.isEmpty() && row.getItem() != null) {
                    try {
                        String filename = row.getItem().getFilename();
                        String result = RenamePopUpScene.getScene().setRenamePopUpScene(filename);

                        if (result != null && !result.isBlank()) {
                            String oldPath = pathField.getText() + "/" + filename;
                            String newPath = pathField.getText() + "/" + result;
                            sftpChannel.rename(oldPath, newPath);
                            new LoadFiles().load(tableView, pathField, sftpChannel);
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    } catch (SftpException e) {
                        System.out.println("Here we have a SftpException");
                    }
                }
            });
            return row;
        });
    }

    private void deleteDirectoryRecursive(ChannelSftp sftpChannel, String path) {
        try {
            Vector<ChannelSftp.LsEntry> items  = sftpChannel.ls(path);

            for(ChannelSftp.LsEntry item : items){
                String fileName = item.getFilename();
                if(".".equals(fileName) || "..".equals(fileName)){
                    continue;
                }

                String fullPath = path+"/"+fileName;
                if(item.getAttrs().isDir()){
                    deleteDirectoryRecursive(sftpChannel, fullPath);
                }
                else {
                    sftpChannel.rm(fullPath);
                }
            }
            sftpChannel.rmdir(path);
        } catch (SftpException e) {
            new Alert(new Error()).show(e.getMessage());
        }
    }
}