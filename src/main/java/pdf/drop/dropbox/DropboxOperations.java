/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf.drop.dropbox;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.CreateFolderErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.sharing.PathLinkMetadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import pdf.drop.alerts.AlertEnum;
import pdf.drop.application.AppAlert;
import pdf.drop.controllers.ControllerFiles;

public class DropboxOperations {

    private DbxClientV2 client;
    private InputStream inputStream;
    private RegisterFile registerFile;

    public DropboxOperations(DbxClientV2 client) {
        this.client = client;
    }

    public String uploadFile(String path, String fileName, File file) throws DbxException, UploadErrorException, IOException {
        ListFolderResult result = null;
        try {
            result = client.files().listFolder(path);
        } catch (DbxException ex) {
            Logger.getLogger(ControllerFiles.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (true) {
            for (Metadata metadata : result.getEntries()) {
                if (metadata.getName().endsWith(".pdf")
                        || metadata.getName().endsWith(".PDF")) {

                    String part1 = metadata.getName().substring(0,
                            metadata.getName().length() - ".pdf".length());
                    String part2 = fileName.substring(0,
                            fileName.length() - ".pdf".length());

                    if (part1.equalsIgnoreCase(part2)) {
                        return null;
                    }
                }
            }

            if (!result.getHasMore()) {
                break;
            }

            try {
            	result = client.files().listFolderContinue(result.getCursor());
            } catch (DbxException ex) {
                Logger.getLogger(ControllerFiles.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        inputStream = new FileInputStream(file);
        FileMetadata metadata = null;
        try {
        	registerFile = new RegisterFile(path + "/" + fileName);
            metadata = client.files().upload(path + "/" + fileName)
                    .uploadAndFinish(inputStream);
        } catch (IOException ex) {
        }
        inputStream.close();
        return metadata.getPathLower();
    }

    public String createNewFolder(String path, String name) throws DbxException {
        try {
        	client.files().createFolder(path + "/" + name);
		} catch (CreateFolderErrorException e) {
            AppAlert.showAndWaitAlert(AlertEnum.FOLDER_CAN_NOT_BE_CREATED);
		}
        return path + "/" + name;
    }
    
    public void closeInputStream() throws IOException {
        inputStream.close();
        if(registerFile != null) {
        	try {
				deleteFileOrFolder(registerFile.getFileUpload());
			} catch (DbxException e) {
			}
        }
     }

    public void deleteFileOrFolder(String path) throws DbxException {
        client.files().delete(path);
    }

    public String shareFile(String path) throws DbxException {
        PathLinkMetadata link = client.sharing().createSharedLink(path);
        return link.getUrl();
    }

    private class RegisterFile {
    	
    	private final String fileUpload;

    	public RegisterFile(String fileUpload) {
    		this.fileUpload = fileUpload;
    	}
    	
    	public String getFileUpload() {
    		return fileUpload;
    	}
    }
}
