/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf.drop.dropbox;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;

public class DropboxAuthorization {
    
    private DbxWebAuthNoRedirect authorizeWeb = null;
    
    public DropboxAuthorization(String authKey, String authPass) throws DbxException { 
        DbxAppInfo appInfo = new DbxAppInfo(authKey, authPass);
        DbxRequestConfig config = new DropboxRequestConfig();
        this.authorizeWeb = new DbxWebAuthNoRedirect(config, appInfo);
    }
    
    public String getAuthorizeTokens(String authorizeKey) throws DbxException {
        return authorizeWeb.finish(authorizeKey).getAccessToken();
    }

    public String getAuthorizeURL() {
        if(authorizeWeb == null) {
            return null;
        }
        return authorizeWeb.start();
    }
   
    
}
