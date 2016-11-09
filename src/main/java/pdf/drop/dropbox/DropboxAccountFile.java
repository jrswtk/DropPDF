/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf.drop.dropbox;

public enum DropboxAccountFile {
    
    DB_FILE(".dbfile"),
    DB_PATH(System.getProperty("user.home") + "/DropPDF"),
    DB_NAME("DropPDF");
    
    private final String string;

    private DropboxAccountFile(String string) {
        this.string = string;
    }
    
    public String getString() {
        return string;
    }
}
