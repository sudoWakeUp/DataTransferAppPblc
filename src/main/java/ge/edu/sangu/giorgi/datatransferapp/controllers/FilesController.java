package ge.edu.sangu.giorgi.datatransferapp.controllers;

import com.jcraft.jsch.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import ge.edu.sangu.giorgi.datatransferapp.Clock;
import ge.edu.sangu.giorgi.datatransferapp.alerts.Alert;
import ge.edu.sangu.giorgi.datatransferapp.alerts.Error;
import ge.edu.sangu.giorgi.datatransferapp.alerts.Info;
import ge.edu.sangu.giorgi.datatransferapp.scenes.LoginScene;
import ge.edu.sangu.giorgi.datatransferapp.transferServices.DownloadFile;
import ge.edu.sangu.giorgi.datatransferapp.transferServices.LoadFiles;
import ge.edu.sangu.giorgi.datatransferapp.transferServices.RowClickListener;
import ge.edu.sangu.giorgi.datatransferapp.usersServices.AuthData;
import ge.edu.sangu.giorgi.datatransferapp.usersServices.CreatorAccess;
import ge.edu.sangu.giorgi.datatransferapp.usersServices.User;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.IntStream;

public class FilesController {

    @FXML private TableView<ChannelSftp.LsEntry> tableView;
    @FXML private TableColumn<ChannelSftp.LsEntry, String> nameColumn;
    @FXML private TableColumn<ChannelSftp.LsEntry, String> sizeColumn;
    @FXML private TableColumn<ChannelSftp.LsEntry, String> typeColumn;
    @FXML private TableColumn<ChannelSftp.LsEntry, String> lastModifiedColumn;
    @FXML private TextField pathField;
    @FXML private FontAwesomeIconView backButton;
    @FXML private FontAwesomeIconView forwardButton;
    @FXML private FontAwesomeIconView downloadButton;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;
    @FXML private Label clockLabel;
    @FXML private Label nameLabel;

    private Session session;
    private ChannelSftp sftpChannel;
    private final Deque<String> historyStack = new ArrayDeque<>();
    private final Deque<String> historyStackForForwardIcon = new ArrayDeque<>();
    private final LoadFiles loadFilesService = new LoadFiles();
    private final RowClickListener rowClickListener = new RowClickListener();

    public void initialize(Session session) {
        this.session = session;
        initializeTable();
        updateBackButton();
        updateForwardButton();
        Clock.getClock(clockLabel);
        List<User> userData = AuthData.getUserData();
        assert userData != null;
        nameLabel.setText(userData.getFirst().getFullName());

        new Thread(() -> {
            try {
                Channel channel = session.openChannel("sftp");
                channel.connect();
                this.sftpChannel = (ChannelSftp) channel;

                Platform.runLater(() -> {
                    String homeDirectory = null;
                    try {
                        homeDirectory = sftpChannel.getHome();
                    } catch (SftpException e) {
                        throw new RuntimeException(e);
                    }
                    pathField.setText(homeDirectory);
                    loadFilesService.load(tableView, pathField, sftpChannel);

                    rowClickListener.setupRowClickListener(
                            tableView,
                            pathField,
                            sftpChannel,
                            historyStack,
                            this::handleDirectoryChanged,
                            this::updateBackButton
                    );
                });
            } catch (Exception e) {
                Platform.runLater(() -> new Alert(new Error()).show("Connection failed"));
            }
        }).start();

        downloadButton.setOnMouseClicked(e ->
                DownloadFile.downloadFile(tableView, pathField, sftpChannel));
    }

    private void handleDirectoryChanged(String newPath, String previousPath) {
        pathField.setText(newPath);
        loadFilesService.load(tableView, pathField, sftpChannel);
    }

    @FXML
    public void initializeTable() {
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFilename()));
        sizeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getAttrs().getSize())));
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAttrs().isDir() ? "Folder" : "File"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        lastModifiedColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                dateFormat.format(new Date((long) cellData.getValue().getAttrs().getMTime() * 1000))
        ));

        nameColumn.setCellFactory(column -> new TableCell<>(){
            private final Image folderImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/ge/edu/sangu/giorgi/datatransferapp/images/blueFolder.png")));
            private final Image fileImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/ge/edu/sangu/giorgi/datatransferapp/images/blueFile.png")));

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    ChannelSftp.LsEntry entry = getTableView().getItems().get(getIndex());
                    if (entry.getAttrs().isDir()) {
                        ImageView icon = new ImageView(folderImg);
                        icon.setFitHeight(16);
                        icon.setFitWidth(16);
                        setGraphic(icon);
                        setText(" " + item);
                    } else {
                        ImageView icon = new ImageView(fileImg);
                        icon.setFitHeight(16);
                        icon.setFitWidth(16);
                        setGraphic(icon);
                        setText(" " + item);
                    }
                }
            }
        });
    }

    @FXML
    private void onPathFieldClick(){
        pathField.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER){
                new LoadFiles().load(tableView, pathField, sftpChannel);
            }
        });
    }

    @FXML
    private void onBackButtonClick() {
        if (!historyStack.isEmpty()) {
            String previousDir = historyStack.pop();
            historyStackForForwardIcon.add(pathField.getText());
            pathField.setText(previousDir);
            loadFilesService.load(tableView, pathField, sftpChannel);
            updateBackButton();
            updateForwardButton();
        }
    }

    @FXML
    private void onForwardButtonClick() {
        if (!historyStackForForwardIcon.isEmpty()) {
            String previousDir = historyStackForForwardIcon.pop();
            historyStack.add(pathField.getText());
            pathField.setText(previousDir);
            loadFilesService.load(tableView, pathField, sftpChannel);
            updateForwardButton();
            updateBackButton();
        }
    }

    @FXML
    private void onLogoutButtonClick() {
        try {
            if (sftpChannel != null) {
                if (sftpChannel.isConnected()) {
                    sftpChannel.exit();
                }
                sftpChannel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
            Stage stage = (Stage) tableView.getScene().getWindow();
            LoginScene.getScene().setLoginScene(stage);

        } catch (IOException e) {
            new Alert(new Error()).show("Scene change error: " + e.getMessage());
        } finally {
            sftpChannel = null;
            session = null;
        }
    }

    public void updateBackButton() {
        backButton.setDisable(historyStack.isEmpty());
    }

    public void updateForwardButton() {
        forwardButton.setDisable(historyStackForForwardIcon.isEmpty());
    }

    //////////////// Drag and Drop /////////////////////

    @FXML
    private void onDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
            statusLabel.setText("Drop files to upload");
        }
    }

    @FXML
    private void onDragExit(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
            statusLabel.setText("");
        }
    }

    @FXML
    private void onDragDrop(DragEvent event) {
        progressBar.setProgress(0);
        statusLabel.setText("Starting upload...");
        List<File> files = event.getDragboard().getFiles();

        new Thread(() -> {
            try {
                for (int i = 0; i < files.size(); i++) {
                    File f = files.get(i);
                    try (FileInputStream fis = new FileInputStream(f)) {
                        sftpChannel.put(fis, pathField.getText() + "/" + f.getName());
                        Platform.runLater(() -> statusLabel.setText(f.getName() + " uploaded"));
                        double finalProgress = (double) (i + 1) / files.size();
                        Platform.runLater(() -> progressBar.setProgress(finalProgress));
                    }
                }
                Platform.runLater(() -> statusLabel.setText("Upload complete"));
                Platform.runLater(() -> loadFilesService.load(tableView, pathField, sftpChannel));
            } catch (SftpException | IOException e) {
                new Alert(new Error()).show(e.getMessage());
            }
        }).start();
    }

    @FXML private void onHomeIconClick(){
        pathField.setText("/home/admin");
        loadFilesService.load(tableView, pathField, sftpChannel);
    }

    @FXML private void onReloadIconClick(){
        loadFilesService.load(tableView, pathField, sftpChannel);
    }

    @FXML private void onUploadButtonClick(){
        FileChooser fileChooser = new FileChooser();
        List<File> files = fileChooser.showOpenMultipleDialog(tableView.getScene().getWindow());

        if(files != null) {
            progressBar.setProgress(0);

            new Thread(() -> {
                try {
                    for (int i = 0; i < files.size(); i++) {
                        File f = files.get(i);
                        try (FileInputStream fis = new FileInputStream(f)) {
                            sftpChannel.put(fis, pathField.getText() + "/" + f.getName());
                            Platform.runLater(() -> statusLabel.setText(f.getName() + " uploaded"));
                            double finalProgress = (double) (i + 1) / files.size();
                            Platform.runLater(() -> progressBar.setProgress(finalProgress));
                        }
                    }
                    Platform.runLater(() -> statusLabel.setText("Upload complete"));
                    Platform.runLater(() -> loadFilesService.load(tableView, pathField, sftpChannel));
                } catch (SftpException | IOException e) {
                    new Alert(new Error()).show(e.getMessage());
                }
            }).start();
    }
    }

    @FXML private void onFolderButtonClick(){
        if(CreatorAccess.checkAccess()){
            new Alert(new Error()).show("You need to be registered and logged in to create a folder");
        }
        else{
            try {
                String folder = pathField.getText() + "/newDirectory";
                sftpChannel.mkdir(folder);
                loadFilesService.load(tableView, pathField, sftpChannel);
                new Alert(new Info()).show("Folder created");

                ObservableList<ChannelSftp.LsEntry> files = FXCollections.observableArrayList(sftpChannel.ls(pathField.getText()));
                int index = IntStream.range(0, files.size())
                                .filter(i -> files.get(i).getFilename().equals("newDirectory"))
                                .findFirst().orElse(-1);
                Platform.runLater(() -> {
                    tableView.getSelectionModel().select(index);
                    tableView.getFocusModel().focus(index);
                    tableView.requestFocus();
                });
            } catch (SftpException e) {
                throw new RuntimeException(e);
            }
        }
    }
}