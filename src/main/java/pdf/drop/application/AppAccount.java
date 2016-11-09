/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf.drop.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import pdf.drop.dropbox.DropboxAccount;
import pdf.drop.files.FileSerialization;


public class AppAccount {

    public static DropboxAccount readDropboxAccount() throws IOException, FileNotFoundException, ClassNotFoundException {
        DropboxAccount dropboxAccount = null;
        File directory = new File(FileSerialization.DIRECTORY);
        if (directory.exists()) {
            File[] files = directory.listFiles(AppFiles.filenameFilter);
            if (files.length >= 1) {
                dropboxAccount = (DropboxAccount) AppFiles.readObject(files[0]);
                DropboxAccount.setDropboxAccount(dropboxAccount);
            }
        }
        return dropboxAccount;
    }

    public static void saveDropboxAccount(DropboxAccount dbAccount) throws IOException {
        String directory = FileSerialization.DIRECTORY;
        String fileName = FileSerialization.FILE_NAME;
        File file = AppFiles.saveFile(directory, fileName);
        AppFiles.saveObject(dbAccount, file);
    }

}
