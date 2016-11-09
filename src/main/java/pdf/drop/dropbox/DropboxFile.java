/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf.drop.dropbox;

import com.dropbox.core.v2.files.Metadata;

public class DropboxFile {
    
    private String name;
    private String path;
    private final Metadata metadata;
    private DropboxFileType fileType;

    public DropboxFile(Metadata metadata) {
        this.metadata = metadata;
        this.name = metadata.getName();
        this.path = metadata.getPathLower();
        this.fileType = recognizeFileType(metadata.getName());
    }
    
    public DropboxFile(String name, String path, Metadata metadata) {
        this.name = name;
        this.path = path;
        this.metadata = metadata;
        this.fileType = recognizeFileType(name);
    }
    
    public final DropboxFileType recognizeFileType(String fileName) {
        if(fileName.endsWith(".pdf") || fileName.endsWith(".PDF")) {
            return DropboxFileType.PDF_FILE;
        }     
        
        if(isFile()) {
            return DropboxFileType.OTHER_FILE;
        }
        
        return DropboxFileType.DIRECTORY;
    }

    public boolean isFile() {
        return metadata.toString()
                .split(":")[1]
                .split(",")[0]
        		.matches("(.*)file(.*)");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setFileType(DropboxFileType fileType) {
        this.fileType = fileType;
    }

    public String getPath() {
        return path;
    }

    public DropboxFileType getFileType() {
        return fileType;
    }

    public Metadata getMetadata() {
        return metadata;
    }
    
    @Override
    public String toString() {
        return "DropboxFile: " + getName() + " | " + getFileType() + " | " + getPath();
    }
    
    
}
