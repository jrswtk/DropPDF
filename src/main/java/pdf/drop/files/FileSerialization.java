package pdf.drop.files;

import pdf.drop.dropbox.DropboxAccountFile;

public class FileSerialization {

    public static final String DIRECTORY = DropboxAccountFile.DB_PATH.getString();
    public static final String FILE_NAME = DropboxAccountFile.DB_NAME.getString()
            + DropboxAccountFile.DB_FILE.getString();
}
