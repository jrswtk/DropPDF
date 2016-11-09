/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf.drop.dropbox;

import javafx.application.HostServices;

public class DropboxHostServices {

    private static DropboxHostServices INSTANCE = null;    

    public static synchronized DropboxHostServices getINSTANCE() {
        return INSTANCE;
    }    
    
    private HostServices hostServices;

    public DropboxHostServices() {
        INSTANCE = this;
    }
    
    public DropboxHostServices(HostServices hostServices) {
        super();
        this.hostServices = hostServices;
    }
    
    public HostServices getHostServices() {
        return hostServices;
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

}
