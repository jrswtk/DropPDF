/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf.drop.controllers;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import pdf.drop.alerts.AlertEnum;
import pdf.drop.application.AppAlert;
import pdf.drop.application.AppImageLoader;
import pdf.drop.dropbox.DropboxAccount;
import pdf.drop.dropbox.DropboxFile;
import pdf.drop.dropbox.DropboxFileType;
import pdf.drop.dropbox.DropboxOperations;
import pdf.drop.files.FilePath;
import pdf.drop.main.Main;

public class ControllerFiles implements Initializable {

    @FXML
    private AnchorPane aPaneMainView;
    @FXML
    private ListView<DropboxFile> listViewDrop;
    @FXML
    private MenuBar menuBarMainView;
    @FXML
    private AnchorPane aPaneShareView;
    @FXML
    private AnchorPane aPaneDropbox;
    @FXML
    private TextField textFieldDropbox;
    @FXML
    private Pane paneDropboxButtons;
    @FXML
    private AnchorPane aPaneFinformation;
    @FXML
    private ImageView imageViewBack;
    @FXML
    private ImageView imageViewNewFolder;
    @FXML
    private ImageView imageViewDeleteFolder;
    @FXML
    private ImageView imageViewHome;
    @FXML
    private Label labelLocalization;
    @FXML
    private Button buttonSave;
    @FXML
    private Button buttonClose;
    @FXML
    private MenuItem menuItemHelp;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DropboxAccount db = DropboxAccount.getDropboxAccount();
        clientV2 = db.getUserClient();

        dropboxPath = new DropboxPath();
        operations = new DropboxOperations(clientV2);

        initializeList();
        initializeBack();
        initializeHome();
        initializeNewFolder();
        initializeDeleteFolder();
        initializeToolTips();

        loadPath("");
    }

    @FXML
    private void buttonSaveAction(ActionEvent event) throws Exception {
        saveMergeFileInDropbox();
    }

    @FXML
    private void textFieldDropboxKeyPressed(KeyEvent event) {
        if(KeyCode.ENTER == event.getCode()) {
            saveMergeFileInDropbox();
        }
    }

    @FXML
    private void buttonCloseAction(ActionEvent event) {
        Stage stage = (Stage) buttonClose.getScene().getWindow();
        stage.close();
    }

    private void initializeToolTips() {
        Tooltip.install(imageViewBack, new Tooltip("Wstecz"));
        Tooltip.install(imageViewDeleteFolder, new Tooltip("Usuń"));
        Tooltip.install(imageViewNewFolder, new Tooltip("Nowy folder"));
        Tooltip.install(imageViewHome, new Tooltip("Katalog główny"));
    }

    private void initializeBack() {
        imageViewBack.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String path = dropboxPath.getLastPath();
                loadPathTask(path);
            }
        });
    }

    private void initializeNewFolder() {
        imageViewNewFolder.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String folderName = inputFolderName();
                try {
                    operations.createNewFolder(dropboxPath.getActualPath(),
                            folderName);
                    loadPathTask(dropboxPath.getActualPath());
                } catch (DbxException ex) {
                    Logger.getLogger(ControllerFiles.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void initializeDeleteFolder() {
        imageViewDeleteFolder.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String name = listViewDrop.getSelectionModel().getSelectedItem()
                        .getName();
                String deletePath = dropboxPath.getActualPath() + "/" + name;
				
                try {
                    if (showConfirmDeleteDialog()) {
                        operations.deleteFileOrFolder(deletePath);
                    }
                } catch (DbxException ex) {
                    Logger.getLogger(ControllerFiles.class.getName()).log(Level.SEVERE, null, ex);
                }
                loadPathTask(dropboxPath.getActualPath());
            }
        });
    }

    private void initializeHome() {
        imageViewHome.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String homePath = dropboxPath.getRootPath();
                loadPathTask(homePath);
            }
        });
    }

    private void initializeList() {
        listViewDrop.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        listViewDrop.setCellFactory(new Callback<ListView<DropboxFile>, ListCell<DropboxFile>>() {

            @Override
            public ListCell<DropboxFile> call(ListView<DropboxFile> param) {
                ListCell<DropboxFile> cell;
                cell = new ListCell<DropboxFile>() {
                    ImageView imageview = new ImageView();

                    @Override
                    protected void updateItem(DropboxFile item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setItem(item);
                            HBox hBox = new HBox();
                            hBox.setSpacing(10);
                            VBox vBox = new VBox();
                            vBox.getChildren().add(new Label(item.getName()));

                            Image image = null;

                            if (null != item.getFileType()) {
                                switch (item.getFileType()) {
                                    case DIRECTORY:
                                        image = new AppImageLoader().getImage("dropfolder");
                                        vBox.getChildren().add(new Label("folder"));
                                        break;
                                    case PDF_FILE:
                                        image = new AppImageLoader().getImage("droppdf");
                                        vBox.getChildren().add(new Label("plik pdf"));
                                        break;
                                    default:
                                        image = new AppImageLoader().getImage("dropfile");
                                        vBox.getChildren().add(new Label("plik"));
                                        break;
                                }
                            }

                            imageview.setImage(image);

                            hBox.getChildren().addAll(imageview, vBox);
                            setGraphic(hBox);
                        }
						
                        if (empty) {
                            setGraphic(null);
                        }
                    }
                };
                return cell;
            }
        });

        listViewDrop.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    if (listViewDrop.getSelectionModel().isEmpty()) {
                        return;
                    }

                    DropboxFile dFile = listViewDrop.getSelectionModel()
                            .getSelectedItem();

                    if (dFile.getFileType() == DropboxFileType.DIRECTORY) {
                        loadPathTask(dFile.getPath());
                        dropboxPath.addPath(dFile.getPath());
                    }
                }
            }
        });
    }

    private void saveMergeFileInDropbox() {
        
        if(textFieldDropbox.getText().equals("")) {
            AppAlert.showAndWaitAlert(AlertEnum.FILENAME_IS_INVALID);
            return;
        }
        
        if (stage == null) {
            stage = new Stage();
            stage.getIcons().add(new AppImageLoader().getImage("icon"));
        }

        showLoadingStage(stage);

        uploader = new Task<String>() {
            @Override
            protected void cancelled() {
                try {
                    operations.closeInputStream();
                } catch (IOException | NullPointerException ex) {
                }
            }
            
            @Override
            protected String call() throws Exception {

                String link = null;

                if (!textFieldDropbox.getText().equals("")) {
                    String fileName = textFieldDropbox.getText();
                    if (!fileName.endsWith(".pdf")
                            || !fileName.endsWith(".PDF")) {
                        fileName += ".pdf";
                    }
                    File file = new File(FilePath.TEMP_MERGE_PDF);
                    if (file.exists()) {
                        final String path = dropboxPath.getActualPath();
                        String dPath = null;
                        try {
                             dPath = operations.uploadFile(path, fileName,
                                    file);

                            if (dPath == null) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        stage.hide();
                                        AppAlert.showAndWaitAlert(AlertEnum.FILENAME_CAN_NOT_BE_SENT);
                                    }
                                });
                                return null;
                            }
                        } catch (DbxException ex) {
                            Logger.getLogger(ControllerFiles.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(ControllerFiles.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        ControllerMain.getControllerMain().setPathToLink(dPath);
                        loadPathTask(dropboxPath.getActualPath());
                        
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                stage.hide();
                                if(!isCancelled()) {
                                    AppAlert.showAndWaitAlert(AlertEnum.FILE_WAS_SENT);
                                }
                            }
                        });
                    }
                }
                return link;
            }
        };

        Thread uploading = new Thread(uploader);
        uploading.start();
    }

    private void loadPathTask(String folder) {
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Main.getScene().getRoot().setCursor(Cursor.WAIT);
            }
        });
        
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                loadPath(folder);
                return null;
            }

            @Override
            protected void done() {
                Main.getScene().getRoot().setCursor(Cursor.DEFAULT);
            }
        };
        
        Platform.runLater(task);
    }

    private void loadPath(String folder) {
        listViewDrop.setItems(null);
        ObservableList<DropboxFile> data = FXCollections.observableArrayList();

        dropboxPath.addPath(folder);
        ListFolderResult result = null;
        try {
            result = clientV2.files().listFolder(folder);
        } catch (DbxException ex) {
            Logger.getLogger(ControllerFiles.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (true) {
            for (Metadata metadata : result.getEntries()) {
                data.add(new DropboxFile(metadata));
            }

            if (!result.getHasMore()) {
                listViewDrop.setPlaceholder(new Label("Folder jest pusty"));
                break;
            }

            try {
                result = clientV2.files().listFolderContinue(result.getCursor());
            } catch (DbxException ex) {
                Logger.getLogger(ControllerFiles.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        listViewDrop.setItems(data);
        setLoacalizationInfo(dropboxPath.getActualPath().equals(
                dropboxPath.getRootPath())
                        ? "folder główny"
                        : dropboxPath.getActualPath());
    }

    private String inputFolderName() {
        String folderName = "Nowy folder";
        TextInputDialog dialog = new TextInputDialog(folderName);
        dialog.setTitle("Tworzenie folderu");
        dialog.setHeaderText("Tworzenie nowego folderu na koncie Dropbox.");
        dialog.setContentText("Wprowadź nazwę foldru:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            folderName = result.get();
        }

        return folderName;
    }

    private boolean showConfirmDeleteDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuwanie pliku / folderu");
        alert.setHeaderText("Czy napewno chcesz trwale usunąć wybrany plik / folder ?");
        alert.setContentText("Kliknij Ok, aby potwierdzić.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }

    private void showLoadingStage(Stage stage) {
        Image image = new Image(getClass().getResourceAsStream(
                "/images/loading.gif"));
        ImageView imageView = new ImageView(image);
        Button cancel = new Button("Przerwij");
        cancel.setPrefWidth(150.0);
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (uploader.isRunning()) {
                    uploader.cancel();
                }
                ((Node) (event.getSource())).getScene().getWindow().hide();
            }
        });
        cancel.setStyle(
                "-fx-border-color: transparent; "
                + "-fx-background-color: #0040ff; "
                + "-fx-font-family: Arial; "
                + "-fx-font-weight: bold; "
                + "-fx-text-fill: white; ");

        VBox layout = new VBox();
        layout.getChildren().addAll(imageView, cancel);
        layout.setStyle("-fx-background-color: #818181;");
        layout.setPadding(new Insets(10, 10, 10, 10));
        stage.setScene(new Scene(layout, 170, 200));
        stage.show();
    }

    private void setLoacalizationInfo(String path) {
        String localization = "Dropbox lokalizacja: ";
        if (path != null) {
            localization += path;
        }
        labelLocalization.setText(localization);
    }

    private DropboxPath dropboxPath;
    private DropboxOperations operations;
    private DbxClientV2 clientV2;

    private Task<String> uploader = null;
    private Stage stage = null;

    @FXML
    private void menuItemHelp(ActionEvent event) {
        AppAlert.showAndWaitAlert(AlertEnum.HELP_SENT_FILE);
    }

    private class DropboxPath {

        private final List<String> paths;
        private String rootPath;

        public DropboxPath() {
            paths = new ArrayList<>();
            rootPath = "";
        }

        public void addPath(String path) {
            if (paths.isEmpty()) {
                rootPath = path;
            }
            if (paths.contains(path)) {
                return;
            }
            paths.add(path);
        }

        public String getLastPath() {
            if (paths.size() > 1) {
                String actualPath = paths.get(paths.size() - 1);
                paths.remove(actualPath);
            }
            return paths.get(paths.size() - 1);
        }

        public String getActualPath() {
            return paths.get(paths.size() - 1);
        }

        public String getRootPath() {
            return rootPath;
        }

        public String toRootPath() {
            paths.clear();
            return rootPath;
        }
    }

}
