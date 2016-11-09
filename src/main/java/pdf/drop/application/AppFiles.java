/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf.drop.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import pdf.drop.dropbox.DropboxAccountFile;
import pdf.drop.files.FilePath;
import pdf.drop.files.FileSize;

public class AppFiles {

    public static void saveFile(File sourceFile, Stage stage) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz plik PDF");
        ExtensionFilter filter = new FileChooser.ExtensionFilter(
			"Pliki PDF", "*.pdf");
			
        fileChooser.getExtensionFilters().add(filter);
        File fileOut = fileChooser.showSaveDialog(stage);
		
        if (fileOut != null) {
            InputStream inputStream = new FileInputStream(sourceFile);
            OutputStream outputStream = new FileOutputStream(fileOut);
            byte[] buf = new byte[1024];
            int read;
			
            while ((read = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, read);
            }
			
            inputStream.close();
            outputStream.close();
        }
    }

    public static File loadDirectory() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Wczytaj katalog");
        String defaultPath = System.getProperty("user.home");
        chooser.setInitialDirectory(new File(defaultPath));
        File directory = chooser.showDialog(null);
        return directory;
    }

    public static FilenameFilter filenameFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            String suffix = name.substring(name.lastIndexOf('.'));
            return suffix.equals(DropboxAccountFile.DB_FILE.getString());
        }
    };

    public static void saveObject(Object object, File file) throws IOException {
        FileOutputStream fout = new FileOutputStream(file);
        try (ObjectOutputStream oos = new ObjectOutputStream(fout)) {
            oos.writeObject(object);
            oos.close();
        }
    }

    public static Object readObject(File file) throws IOException, ClassNotFoundException {
        FileInputStream fin = new FileInputStream(file);
        Object object = null;
        try (ObjectInputStream ois = new ObjectInputStream(fin)) {
            object = ois.readObject();
            ois.close();
        }
        return object;
    }

    public static void moveFile(File sourceFile, String destPath) throws IOException {
        Files.copy(sourceFile.toPath(), new FileOutputStream(destPath));
    }

    public static File getFile(String path, String suffix) {
        File file = getFile(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files.length > 0) {
                for (File f : files) {
                    if (f.getName().endsWith(suffix)) {
                        return f;
                    }
                }
            }
        }
        return null;
    }

    public static void createTempFiles() throws IOException {
        File file = new File(FilePath.TEMP_DIRECTORY);
        System.out.println(file.getPath());
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(FilePath.TEMP_MERGE_PDF);
        System.out.println(file.getPath());
        if (!file.exists()) {
            file.createNewFile();
        }
        file = new File(FilePath.TEMP_CONVERT_PDF);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    public static void removeTempDirectory() {
        File toRemove = new File(FilePath.TEMP_DIRECTORY);
        toRemove.delete();
    }

    public static File getFile(String path) {
        return new File(path);
    }

    public static File saveFile(String directory, String fileName) throws IOException {
        File file = new File(directory);
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(file.getPath() + "/" + fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    public static String getSuffix(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static String getName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static String getSize(long fileSize) {
        return FileSize.convert(fileSize);
    }

}
