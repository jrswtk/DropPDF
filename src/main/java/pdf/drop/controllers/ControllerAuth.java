/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf.drop.controllers;

import com.dropbox.core.DbxException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import pdf.drop.alerts.AlertEnum;
import pdf.drop.application.AppAlert;
import pdf.drop.dropbox.DropboxHostServices;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import pdf.drop.application.AppCipher;
import pdf.drop.application.AppAccount;
import pdf.drop.application.AppFiles;
import pdf.drop.dropbox.DropboxAccountFile;
import pdf.drop.dropbox.DropboxAuthorization;
import pdf.drop.dropbox.DropboxAccount;
import pdf.drop.files.FileSerialization;

public class ControllerAuth implements Initializable {

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
    private Hyperlink hyperLinkDropboxWebsite;
    @FXML
    private TextField textFieldKey;
    @FXML
    private TextField textFieldPass;
    @FXML
    private Hyperlink hyperlinkDropboxAuthLink;
    @FXML
    private Button buttonCancel;
    @FXML
    private Button buttonSave;
    @FXML
    private MenuItem menuItemImport;
    @FXML
    private MenuItem menuItemClose;
    @FXML
    private MenuItem menuItemHelp;
    @FXML
    private ImageView imageViewDropbox;
    @FXML
    private CheckBox checkBoxAuthor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbAccount = DropboxAccount.getDropboxAccount();
        disableEditComponents(!checkBoxAuthor.isSelected());
    }

    @FXML
    private void hyperLinkDropboxWebsiteClicked(MouseEvent event) {
        DropboxHostServices.getINSTANCE().getHostServices()
                .showDocument("https://www.dropbox.com/developers/apps");
    }

    @FXML
    private void hyperlinkDropboxAuthLinkClicked(MouseEvent event) {
        if (!authLink.equals("")) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(authLink);
            clipboard.setContent(content);
        }
    }

    @FXML
    private void textFieldKeyTextReleased(KeyEvent event) {
        setAuthorizationData();
    }

    @FXML
    private void textFieldPassReleased(KeyEvent event) {
        setAuthorizationData();
    }

    @FXML
    private void textFieldKeyTextClicked(MouseEvent event) {
        setAuthorizationData();
    }

    @FXML
    private void textFieldPassClicked(MouseEvent event) {
        setAuthorizationData();
    }

    @FXML
    private void menuItemImportAction(ActionEvent event) throws IOException, ClassNotFoundException {
        File readFile = importUserAuth();
        if (readFile != null) {
            dbAccount = (DropboxAccount) AppFiles.readObject(readFile);
            String destFile = FileSerialization.DIRECTORY
                    + "/"
                    + FileSerialization.FILE_NAME;

            AppFiles.moveFile(readFile, destFile);
            if (dbAccount.isAccess()) {
                dbAccount.setConnected(true);
                AppAccount.saveDropboxAccount(dbAccount);
            }
        }
        setAccountStatus(dbAccount.isAccess());
    }

    @FXML
    private void menuItemCloseAction(ActionEvent event) {
        closeStage();
    }

    @FXML
    private void menuItemHelpAction(ActionEvent event) {
        AppAlert.showAndWaitAlert(AlertEnum.HELP_AUTH);
    }

    @FXML
    private void imageViewDropboxClicked(MouseEvent event) {
        DropboxHostServices.getINSTANCE().getHostServices()
                .showDocument("https://www.dropbox.com");
    }

    @FXML
    private void buttonCancelAction(ActionEvent event) {
        closeStage();
    }

    @FXML
    private void checkBoxAuthorAction(ActionEvent event) {
        disableEditComponents(!checkBoxAuthor.isSelected());
    }

    @FXML
    private void buttonSaveAction(ActionEvent event) throws DbxException, 
			FileNotFoundException, IOException, ClassNotFoundException, 
			InterruptedException, ExecutionException {
				
        Task task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return peformAuthorized();
            }
        };
		
        new Thread(task).start();
		
        while (!task.isDone()) {
        }
		
        boolean isAuthorized = (Boolean) task.get();
		
        if (isAuthorized) {
            AppAccount.saveDropboxAccount(dbAccount);
            AppAlert.showAndWaitAlert(AlertEnum.CONF_FILE_WAS_CREATED);
        }
		
        setAccountStatus(isAuthorized);
        closeStage();
    }

    private File importUserAuth() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importowanie pliku konfiguracyjnego");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Plik konfiguracyjny DropPDF", "*"
                + DropboxAccountFile.DB_FILE.getString()));

        File file = fileChooser.showOpenDialog(null);
        return file;
    }

    private boolean peformAuthorized() throws IOException, ClassNotFoundException, NoSuchPaddingException {
        if (!textFieldDropbox.getText().equals("")) {
            userAuth = textFieldDropbox.getText();
			
            if (checkBoxAuthor.isSelected()) {
                try {
                    userAuth = dropboxAuth.getAuthorizeTokens(userAuth);
                } catch (DbxException ex) {
                    System.err.println("Błąd przy autoryzacji");
                    return false;
                }
            }
			
            try {
                String tokens = null;
                try {
                    tokens = AppCipher.encrytpTokens(userAuth);
                } catch (UnsupportedEncodingException ex) {
                } catch (InvalidKeyException ex) {
                } catch (IllegalBlockSizeException ex) {
                } catch (BadPaddingException ex) {
                } catch (Base64DecodingException ex) {
                } catch (Exception ex) {
                }
                dbAccount.setTokens(tokens);
                dbAccount.setUserInformation();
                dbAccount.setConnected(true);
            } catch (DbxException ex) {
                System.err.println("Nieprawidłowe tokeny");
                return false;
            }
			
        } else {
            return false;
        }
        return true;
    }

    private void setAuthorizationData() {
        if (!textFieldKey.getText().equals("")) {
            authKey = textFieldKey.getText();
        }
        if (!textFieldPass.getText().equals("")) {
            authPass = textFieldPass.getText();
        }
        if (authKey != null && authPass != null) {
            authLink = getAuthURL();
            hyperlinkDropboxAuthLink.setText(authLink.substring(0, 60) + "...");
            hyperlinkDropboxAuthLink.setTooltip(new Tooltip(authLink));
        }
    }

    private void setAccountStatus(boolean isAuthorized) {
        String status = "Dropbox: ";
        if (isAuthorized) {
            status += "aplikacja została zautoryzowana";
        } else {
            status += "aplikacja nie została zautoryzowana";
        }
        labelDropbox.setText(status);
    }

    private String getAuthURL() {
        try {
            dropboxAuth = new DropboxAuthorization(authKey, authPass);
        } catch (DbxException ex) {
            Logger.getLogger(ControllerAuth.class.getName()).log(Level.SEVERE, null, ex);
        }
		
        return dropboxAuth.getAuthorizeURL();
    }

    private void closeStage() {
        Stage stage = (Stage) buttonCancel.getScene().getWindow();
        stage.onCloseRequestProperty();
        stage.close();
    }

    private void disableEditComponents(boolean isDisable) {
        textFieldKey.setDisable(isDisable);
        textFieldPass.setDisable(isDisable);
        hyperLinkDropboxWebsite.setDisable(isDisable);
        hyperlinkDropboxAuthLink.setDisable(isDisable);
    }

    private String authKey = null;
    private String authPass = null;
    private String authLink = null;
    private String userAuth = null;

    private DropboxAuthorization dropboxAuth = null;
    private DropboxAccount dbAccount = null;

}
