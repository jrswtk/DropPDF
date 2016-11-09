/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf.drop.dropbox;

import com.dropbox.core.DbxRequestConfig;
import java.io.Serializable;
import java.util.Locale;

public class DropboxRequestConfig extends DbxRequestConfig implements Serializable {
       
    public DropboxRequestConfig() {
        super("Authorization App", Locale.getDefault().toString());
    }

}
