package ge.edu.sangu.giorgi.datatransferapp.transferServices;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import ge.edu.sangu.giorgi.datatransferapp.alerts.Alert;
import ge.edu.sangu.giorgi.datatransferapp.alerts.Error;
import ge.edu.sangu.giorgi.datatransferapp.alerts.Info;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Vector;

public class DownloadFile {

    private static File lastSelectedDirectory = new File(System.getProperty("user.home"));

    public static void downloadFile(TableView<ChannelSftp.LsEntry> tableView,
                                    TextField pathField,
                                    ChannelSftp sftpChannel) {
        ObservableList<ChannelSftp.LsEntry> selectedItems = tableView.getSelectionModel().getSelectedItems();

        if (selectedItems == null) {
            new Alert(new Info()).show("Please, select a file or folder to download");
            return;
        }

        DirectoryChooser localDir = new DirectoryChooser();
        localDir.setInitialDirectory(lastSelectedDirectory);
        File selectedDirectory = localDir.showDialog(tableView.getScene().getWindow());

        if (selectedDirectory != null) {
            lastSelectedDirectory = selectedDirectory;

                new Thread(() -> {
                    for(ChannelSftp.LsEntry selectedItem : selectedItems) {
                        String remoteBasePath = pathField.getText();
                        String selectedName = selectedItem.getFilename();
                        String remotePath = remoteBasePath + (remoteBasePath.endsWith("/") ? "" : "/") + selectedName;
                        Path localTargetPath = Paths.get(selectedDirectory.getAbsolutePath(), selectedName);
                        try {
                            if (isDirectory(selectedItem)) {
                                downloadDirectory(sftpChannel, remotePath, localTargetPath);
                            } else {
                                try (InputStream inputStream = sftpChannel.get(remotePath)) {
                                    Files.copy(inputStream, localTargetPath, StandardCopyOption.REPLACE_EXISTING);
                                }
                            }

                            Platform.runLater(() ->
                                    new Alert(new Info()).show("Downloaded to:\n" + localTargetPath)
                            );
                        } catch (Exception e) {
                            Platform.runLater(() ->
                                    new Alert(new Error()).show("Download error:\n" + e.getMessage())
                            );
                        }
                    }
                }).start();
        }
    }

    private static boolean isDirectory(ChannelSftp.LsEntry entry) {
        return entry.getAttrs().isDir();
    }

    private static void downloadDirectory(ChannelSftp sftpChannel, String remoteDirPath, Path localDirPath) throws SftpException, IOException {
        Files.createDirectories(localDirPath);

        Vector<ChannelSftp.LsEntry> entries = sftpChannel.ls(remoteDirPath);
        for (ChannelSftp.LsEntry entry : entries) {
            String name = entry.getFilename();
            if (".".equals(name) || "..".equals(name)) continue;

            String remotePath = remoteDirPath + "/" + name;
            Path localPath = localDirPath.resolve(name);

            if (entry.getAttrs().isDir()) {
                downloadDirectory(sftpChannel, remotePath, localPath);
            } else {
                try (InputStream inputStream = sftpChannel.get(remotePath)) {
                    Files.copy(inputStream, localPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
}
