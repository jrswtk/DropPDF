package pdf.drop.controllers;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.UploadErrorException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;

import pdf.drop.alerts.AlertEnum;
import pdf.drop.application.*;
import pdf.drop.data.DataFile;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import pdf.drop.data.DataFileFormat;
import pdf.drop.data.DataFileSource;
import pdf.drop.dropbox.DropboxAccount;
import pdf.drop.dropbox.DropboxOperations;
import pdf.drop.files.FilePath;
import pdf.drop.files.FileSize;
import pdf.drop.main.Main;
import pdf.drop.merge.MergeOrientation;
import pdf.drop.merge.MergePerformer;
import pdf.drop.merge.MergeSortType;

public class ControllerMain implements Initializable {

    @FXML
    private TableView<DataFile> tableViewFiles;
    @FXML
    private TableColumn<DataFile, String> columnFileName;
    @FXML
    private TableColumn<DataFile, String> columnDate;
    @FXML
    private TableColumn<DataFile, String> columnFormat;
    @FXML
    private TableColumn<DataFile, String> columnnSize;
    @FXML
    private MenuItem menuItemConnect;
    @FXML
    private AnchorPane aPaneMainView;
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
    private Label labelDropbox;
    @FXML
    private SplitPane splitPaneMainView;
    @FXML
    private AnchorPane aPaneLeftView;
    @FXML
    private AnchorPane aPaneOperations;
    @FXML
    private Pane paneButtonSearch;
    @FXML
    private Button buttonSearch;
    @FXML
    private TextField textFieldSearchFiles;
    @FXML
    private MenuButton menuButtonOperations;
    @FXML
    private AnchorPane aPaneRightView;
    @FXML
    private AnchorPane aPaneProgrss;
    @FXML
    private ProgressBar progressBarFiles;
    @FXML
    private MenuItem menuItemAuth;
    @FXML
    private MenuItem menuItemMerge;
    @FXML
    private MenuItem menuItemLoadDirectory;
    @FXML
    private ListView<DataFile> listViewFiles;
    @FXML
    private Button buttonSendMergeFile;
    @FXML
    private Button buttonGetLink;
    @FXML
    private MenuItem menuItemSaveFile;
    @FXML
    private Label labelInfoFiles;
    @FXML
    private MenuItem menuItemReadFiles;
    @FXML
    private Button buttonCopy;
    @FXML
    private MenuItem menuItemHelp;
    @FXML
    private RadioMenuItem radioItemVertical;
    @FXML
    private ToggleGroup pageRotate;
    @FXML
    private RadioMenuItem radioItemHorizonal;
    @FXML
    private RadioMenuItem radioMenuItemA1;
    @FXML
    private ToggleGroup pageSize;
    @FXML
    private RadioMenuItem radioMenuItemA2;
    @FXML
    private RadioMenuItem radioMenuItemA3;
    @FXML
    private RadioMenuItem radioMenuItemA4;
    @FXML
    private RadioMenuItem radioMenuItemB1;
    @FXML
    private RadioMenuItem radioMenuItemB2;
    @FXML
    private RadioMenuItem radioMenuItemB3;
    @FXML
    private RadioMenuItem radioMenuItemB4;
    @FXML
    private RadioMenuItem radioMenuItemAsc;
    @FXML
    private RadioMenuItem radioMenuItemDesc;
    @FXML
    private CheckMenuItem checkMenuItemOpen;
    @FXML
    private SeparatorMenuItem sepMenuItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataTableSource = new DataFileSource();
        dataListSource = new DataFileSource();
        initializeList();
        initializeTable();

        try {
            initializeStatusMergeFile(false);
            initializeStatusDropbox(true);
            initializeMenuItems();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ControllerMain.class.getName()).log(Level.SEVERE, null, ex);
        }

        ControllerMain.controllerMain = this;
    }

    @FXML
    private void menuItemAuthAction(ActionEvent event) throws IOException {
        openAuthorized();
    }

    @FXML
    private void buttonCopyAction(ActionEvent event) {
        if(!checkConnectionWithDropbox()) {
            return;
        }
        if (textFieldDropbox.getText().equals("")) {
            AppAlert.showAndWaitAlert(AlertEnum.LINK_CAN_NOT_BE_COPIED);
            return;
        }
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(textFieldDropbox.getText());
        clipboard.setContent(content);
    }

    @FXML
    private void menuItemSaveFileAction(ActionEvent event) throws IOException {
        File file = new File(FilePath.TEMP_MERGE_PDF);
        if (file.exists()) {
            Stage stage = (Stage) buttonGetLink.getScene().getWindow();
            AppFiles.saveFile(file, stage);
        } else {
            AppAlert.showAndWaitAlert(AlertEnum.FILE_CAN_NOT_BE_SAVE);
        }
    }

    @FXML
    private void menuItemLoadDirectoryAction(ActionEvent event) {
        File directory = AppFiles.loadDirectory();
        if (directory != null && directory.isDirectory()) {
            addFilesToList(true, directory);
            addDataToList();
        }
    }

    @FXML
    private void menuItemConnectAction(final ActionEvent event) throws IOException, ClassNotFoundException {
        if (dbOptions == AppOptions.CONNECT) {
            readUserAccount();
            if (dbAccount != null) {
                dbAccount.setConnected(true);
            } else {
                AppAlert.showAndWaitAlert(AlertEnum.CONF_FILE_IS_NOT_UNAVAILABLE);
            }
        }
        if (dbOptions == AppOptions.DISCONNECT) {
            dbAccount.setConnected(false);
        }
        AppAccount.saveDropboxAccount(dbAccount);
        initializeMenuItems();
        initializeStatusDropbox(false);
    }

    @FXML
    private void buttonGetLinkAction(ActionEvent event) throws DbxException {
        if(!checkConnectionWithDropbox()) {
            return;
        }
        if (pathToLink == null) {
            AppAlert.showAndWaitAlert(AlertEnum.LINK_CAN_NOT_BE_GENERATED);
            return;
        }
        if (isAccess() && isConnected()) {
            textFieldDropbox.setText("");
            DbxClientV2 client = dbAccount.getUserClient();
            DropboxOperations operations = new DropboxOperations(client);
            Task<String> linker = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    String link = operations.shareFile(pathToLink);
                    textFieldDropbox.setText(link);
                    return pathToLink;
                }
            };

            new Thread(linker).start();
        }
    }

    @FXML
    private void buttonSendMergeFileAction(ActionEvent event) throws DbxException, UploadErrorException, IOException {
        if(!checkConnectionWithDropbox()) {
            return;
        }
        if(!new File(FilePath.TEMP_MERGE_PDF).exists()) {
            AppAlert.showAndWaitAlert(AlertEnum.FILE_CAN_NOT_BE_SENT);
            return;
        }
        Platform.runLater(() -> Main.getScene().setCursor(Cursor.WAIT));
        
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                openFiles();
                Main.getScene().setCursor(Cursor.DEFAULT);
                return null;
            }
        };

        Platform.runLater(task);
    }
    
    @FXML
    private void menuItemMergeAction(ActionEvent event) throws DbxException, FileNotFoundException, Exception {
        
        DataFileSource source = null;
        if (tableViewFiles.getItems().isEmpty()) {
            AppAlert.showAndWaitAlert(AlertEnum.FILES_CAN_NOT_BE_MERGE);
            return;
        }

        if (!tableViewFiles.getSelectionModel().isEmpty()) {
            ObservableList selected = tableViewFiles.getSelectionModel()
                    .getSelectedItems();
            source = new DataFileSource();
            tableViewFiles.getSelectionModel().getSelectedItems();
            source.createSource(dataTableSource, selected);
        }
        source = source == null ? dataTableSource : source;
        performer = new MergePerformer(source, getOrientation(), getPDFSize(), getSortType());
        progressBarFiles.progressProperty().bind(performer.progressProperty());
                
        Thread merge = new Thread(performer);
        merge.start();
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if(!performer.isDone()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ControllerMain.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        try {
                            
                            try {
                            	if(performer.get().exists()) {
									initializeStatusMergeFile(true);
								}
								if(checkMenuItemOpen.isSelected()) {
									Desktop.getDesktop().open(performer.get());
								}
							} catch (ExecutionException | InterruptedException e) {
								Platform.runLater(new Runnable() {
									
									@Override
									public void run() {
										progressBarFiles.progressProperty().unbind();
										progressBarFiles.setProgress(0.0);
										try {
											initializeStatusMergeFile(false);
										} catch (ClassNotFoundException | IOException e) {
										}
									}
								});
							}
                            
                            return;
                        } catch (IOException | ClassNotFoundException ex) {
                        	System.out.println("Bład");
                        }
                    }
                }
                    
            }
        });
    }

    @FXML
    private void menuItemReadFilesAction(ActionEvent event) {
        readDataToTable();
    }

    @FXML
    private void buttonSearchAction(ActionEvent event) {
        findInList();
    }

    @FXML
    private void buttonSearchKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            findInList();
        }
    }

    @FXML
    private void textFieldSearchFilesKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            findInList();
        }
    }
    
    @FXML
    private void menuItemHelpAction(ActionEvent event) {
        AppAlert.showAndWaitAlert(AlertEnum.HELP_DROPPDF);
    }

    @FXML
    private void listViewFilesClicked(MouseEvent event) {
    }
    
    private MergeOrientation getOrientation() {
        if(radioItemVertical.isSelected()) {
            return MergeOrientation.VERICAL;
        }
        return MergeOrientation.HORIZONTAL;
    }

    private void initializeList() {
        listViewFiles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ContextMenu menu = new ContextMenu();
        MenuItem addToTable = new MenuItem("Dodaj");
        addToTable.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!listViewFiles.getSelectionModel().isEmpty()) {
                    for (DataFile dataFile : listViewFiles.getSelectionModel()
                            .getSelectedItems()) {

                        File file = dataListSource.getEqualFile(dataFile);
                        addFile(false, file);
                        addDataToTable();
                    }
                }
            }
        });

        listViewFiles.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {

                    DataFile selected = listViewFiles.getSelectionModel()
                            .getSelectedItem();

                    if (selected != null) {
                        File file = dataListSource.getEqualFile(selected);
                        addFile(false, file);
                        addDataToTable();
                    }
                }
            }
        });

        menu.getItems().add(addToTable);
        listViewFiles.setContextMenu(menu);
    }

    private void initializeTable() {
        columnFileName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        columnFormat.setCellValueFactory(new PropertyValueFactory<>("format"));
        columnnSize.setCellValueFactory(new PropertyValueFactory<>("size"));

        columnFormat.setCellFactory(new Callback<TableColumn<DataFile, String>, 
                TableCell<DataFile, String>>() {

            @Override
            public TableCell<DataFile, String> call(TableColumn<DataFile, String> param) {
                TableCell<DataFile, String> cell = new TableCell<DataFile, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        if (item == null) {
                            setGraphic(null);
                            return;
                        }
                        ImageView imageview = new ImageView();
                        AppImageLoader imageLoader = new AppImageLoader();
                        VBox vBox = new VBox();
                        vBox.setAlignment(Pos.CENTER);
                        imageview.setFitHeight(15);
                        imageview.setFitWidth(38);
                        Image image = imageLoader.getImage(item.toUpperCase());
                        imageview.setImage(image);
                        vBox.getChildren().add(imageview);
                        setGraphic(vBox);

                        try {
                            initializeStatusMergeFile(false);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    if(progressBarFiles.getProgress() != 0) {
                                        try {
                                             progressBarFiles.setProgress(0.0);
                                        } catch (java.lang.RuntimeException e) {
                                        }
                                    }
                                }
                            });
                        } catch (IOException | ClassNotFoundException ex) {
                            Logger.getLogger(ControllerMain.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                };
                return cell;
            }
        });

        tableViewFiles.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {

                    DataFile selected = tableViewFiles.getSelectionModel()
                            .getSelectedItem();
                    if (selected != null) {
                        System.out.println("select : " + selected);
                    }
                }
            }
        });

        tableViewFiles.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                DataFile selected = tableViewFiles.getSelectionModel().getSelectedItem();
                if (selected != null) {

                    Dragboard db = tableViewFiles.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(selected.getName());
                    db.setContent(content);
                    event.consume();
                }
            }
        });

        tableViewFiles.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getDragboard().hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            }
        });

        tableViewFiles.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    for (File file : db.getFiles()) {
                        if (file.isDirectory()) {
                            addFilesToList(false, file);
                        }

                        if (DataFileFormat.compare(AppFiles.getSuffix(
                                file.getName()))) {

                            addFile(false, file);
                        }
                    }
                    if (dataTableSource.getDataFiles(getSortType()).isEmpty()) {
                        return;
                    }
                    addDataToTable();
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });

        tableViewFiles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        tableViewFiles.setRowFactory(new Callback<TableView<DataFile>, TableRow<DataFile>>() {
            @Override
            public TableRow<DataFile> call(TableView<DataFile> tableView) {
                final TableRow<DataFile> row = new TableRow<>();
                final ContextMenu contextMenu = new ContextMenu();
                final MenuItem removeMenuItem = new MenuItem("Usuń");
                removeMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        removeInTable();
                    }
                });
                contextMenu.getItems().add(removeMenuItem);
                row.contextMenuProperty().bind(
                        Bindings.when(row.emptyProperty())
                        .then((ContextMenu) null)
                        .otherwise(contextMenu)
                );
                return row;
            }
        });
        
        tableViewFiles.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(KeyCode.DELETE == event.getCode()) {
                    removeInTable();
                }
            }
        });

    }

    private void findInList() {
        listViewFiles.getSelectionModel().clearSelection();
        String toFind = textFieldSearchFiles.getText();
        if (toFind.equals("")) {
            return;
        }
        findFile(toFind);
    }

    private void findFile(String text) {
        for (DataFile dataFile : listViewFiles.getItems()) {
            if (dataFile.getName().toLowerCase().startsWith(text.toLowerCase())) {
                listViewFiles.getSelectionModel().select(dataFile);
            }
        }
    }

    private void readDataToTable() {
        FileChooser chooser = new FileChooser();

        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(
                        "Wszystkie pliki",
                        DataFileFormat.DOCX.getExtension(),
                        DataFileFormat.JPG.getExtension(),
                        DataFileFormat.GIF.getExtension(),
                        DataFileFormat.PNG.getExtension(),
                        DataFileFormat.PDF.getExtension()),
                
                new FileChooser.ExtensionFilter(
                        "Format: docx", 
						DataFileFormat.DOCX.getExtension()),
                new FileChooser.ExtensionFilter(
                        "Format: jpg", 
						DataFileFormat.JPG.getExtension()),
                new FileChooser.ExtensionFilter(
                        "Format: gif", 
						DataFileFormat.GIF.getExtension()),
                new FileChooser.ExtensionFilter(
                        "Format: png", 
						DataFileFormat.PNG.getExtension()),
                new FileChooser.ExtensionFilter(
                        "Format: pdf", 
						DataFileFormat.PDF.getExtension()));

        List<File> files = chooser.showOpenMultipleDialog(
                buttonGetLink.getScene().getWindow());

        if (files != null && !files.isEmpty()) {
            ObservableList<DataFile> data = FXCollections.observableArrayList();
            for (File file : files) {
                DataFile dataFile = addFile(false, file);
                data.add(dataFile);
            }
            tableViewFiles.getItems().addAll(data);
        }
    }

    private void addDataToTable() {
        ObservableList<DataFile> data = FXCollections.observableArrayList();
        for (DataFile dataFile : dataTableSource.getDataFiles(getSortType()).keySet()) {
            ObservableList<DataFile> check = tableViewFiles.getItems();
            if (!check.contains(data)) {
                data.add(dataFile);
            }
        }
        tableViewFiles.setItems(data);
    }

    private void addDataToList() {
        ObservableList<DataFile> data = FXCollections.observableArrayList();
        for (DataFile dataFile : dataListSource.getDataFiles(getSortType()).keySet()) {
            ObservableList<DataFile> check = tableViewFiles.getItems();
            if (!check.contains(data)) {
                data.add(dataFile);
            }
        }
        listViewFiles.setItems(data);
    }

    private void removeInTable() {
        dataTableSource.remove(tableViewFiles
                .getSelectionModel().getSelectedItems());

        tableViewFiles.getItems().removeAll(tableViewFiles
                .getSelectionModel().getSelectedItems());
    }
    
    private boolean checkConnectionWithDropbox() {
        if(!isAccess() || !isConnected()) {
            AppAlert.showAndWaitAlert(AlertEnum.DROPBOX_IS_DISCONNECT);
            return false;
        }
        return true;
    }

    private void addFilesToList(boolean isList, File directory) {
        if (!directory.isDirectory()) {
            return;
        }
        if (isList) {
            dataListSource.clear();
        }

        for (File file : directory.listFiles()) {
            if (!file.isDirectory()) {
                if (DataFileFormat.compare(AppFiles.getSuffix(file.getName()))) {
                    addFile(isList, file);
                }
            }
        }
    }

    private DataFile addFile(boolean isList, File file) {
        DataFile dataFile = new DataFile(
                AppFiles.getName(file.getName()),
                new SimpleDateFormat("HH:mm:ss, dd.MM.yyyy").format(
                        new Date(file.lastModified())),
                AppFiles.getSuffix(file.getName()),
                AppFiles.getSize(file.length()));

        addDataFileToSource(isList, dataFile, file);

        return dataFile;
    }

    private void addDataFileToSource(boolean isList, DataFile dataFile, File file) {
        if (isList) {
            dataListSource.addDataFile(dataFile, file);
        } else {
            dataTableSource.addDataFile(dataFile, file);
        }
    }
    
    private MergeSortType getSortType() {
    	if(radioMenuItemAsc.isSelected()) {
    		return MergeSortType.NAME_ASC;
    	} else {
    		return MergeSortType.NAME_DESC;
    	}
    }
    
    private Rectangle getPDFSize() {
        Rectangle pdfSize = PageSize.A4;
        
        if(radioMenuItemA1.isSelected()) {
            pdfSize = PageSize.A1;
        }
        else if(radioMenuItemA2.isSelected()) {
            pdfSize = PageSize.A2;
        }
        else if(radioMenuItemA1.isSelected()) {
            pdfSize = PageSize.A3;
        }
        else if(radioMenuItemA4.isSelected()) {
            pdfSize = PageSize.A4;
        }
        else if(radioMenuItemB1.isSelected()) {
            pdfSize = PageSize.B2;
        }
        else if(radioMenuItemB2.isSelected()) {
            pdfSize = PageSize.B2;
        }
        else if(radioMenuItemB3.isSelected()) {
            pdfSize = PageSize.B3;
        }
        else if(radioMenuItemB4.isSelected()) {
            pdfSize = PageSize.B4;
        }
        
        return pdfSize;
    }

    private void openAuthorized() throws IOException {
        Stage stageAuthor = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource(
                "/fxml/DropboxAuth.fxml"));
        stageAuthor.setTitle("Autoryzacja konta Dropbox");
        stageAuthor.setMinWidth(550.0);
        stageAuthor.setMinHeight(370.0);
        stageAuthor.setScene(new Scene(root,
                stageAuthor.getMinWidth(),
                stageAuthor.getMinHeight()));
        stageAuthor.setResizable(false);
        stageAuthor.getIcons().add(new AppImageLoader().getImage("icon"));
        stageAuthor.show();
        stageAuthor.setOnHiding(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                try {
                    initializeStatusDropbox(true);
                    initializeMenuItems();
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(ControllerMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void openFiles() throws IOException {
        Stage stageOpen = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource(
                "/fxml/DropboxFiles.fxml"));
        stageOpen.setTitle("Menedżer plików Dropbox");
        stageOpen.setMinWidth(540.0);
        stageOpen.setMinHeight(340.0);
        stageOpen.setScene(new Scene(root,
                stageOpen.getMinWidth(),
                stageOpen.getMinHeight()));
        stageOpen.setResizable(false);
        stageOpen.getIcons().add(new AppImageLoader().getImage("icon"));
        stageOpen.show();

        Stage stage = (Stage) buttonGetLink.getScene().getWindow();

        stageOpen.setOnShowing(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                stage.hide();
            }
        });

        stageOpen.setOnHiding(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                try {
                    initializeStatusDropbox(true);
                    initializeMenuItems();
                    stage.show();
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(ControllerMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void initializeMenuItems() {
        if (isAccess() && isConnected()) {
            menuItemConnect.setText("Rozłącz");
            dbOptions = AppOptions.DISCONNECT;
        } else {
            menuItemConnect.setText("Połącz");
            dbOptions = AppOptions.CONNECT;
        }
    }

    public void initializeStatusDropbox(boolean isRead) throws IOException, ClassNotFoundException {
        if (isRead) {
            readUserAccount();
        }
        String status = "Dropbox: ";
        if (isAccess() && isConnected()) {
            status += "konto zostało podłączone"
                    + " ("
                    + dbAccount.getUserInformation().getName()
                    + ")";
        } else {
            status += "konto nie zostało podłączone";
        }
        labelDropbox.setText(status);
    }

    public void initializeStatusMergeFile(boolean isMerge) throws IOException, ClassNotFoundException {
        String status = "DropPDF: ";
        if (isMerge) {
            status += "plik został scalony"
                    + " ("
                    + FileSize.convert(
                    new File(FilePath.TEMP_MERGE_PDF).length())
                    + ")";
        } else {
            status += "plik nie został scalony";
        }
        labelInfoFiles.setText(status);
    }

    private void readUserAccount() throws IOException, ClassNotFoundException {
        dbAccount = AppAccount.readDropboxAccount();
    }

    private boolean isAccess() {
        if (dbAccount == null) {
            return false;
        }
        return dbAccount.isAccess();
    }

    private boolean isConnected() {
        if (dbAccount == null) {
            return false;
        }
        return dbAccount.isConnected();
    }

    public void setPathToLink(String pathToLink) {
        this.pathToLink = pathToLink;
    }

    public static ControllerMain getControllerMain() {
        return controllerMain;
    }

    private static ControllerMain controllerMain = null;

    private Task<File> performer = null;
    private DropboxAccount dbAccount = null;
    private AppOptions dbOptions = null;
    private DataFileSource dataTableSource = null;
    private DataFileSource dataListSource = null;
    private String pathToLink = null;
}
