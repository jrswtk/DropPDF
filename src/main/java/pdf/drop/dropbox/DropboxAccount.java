/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf.drop.dropbox;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import pdf.drop.application.AppCipher;

public final class DropboxAccount implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private DropboxInformation userInforation = null;
    private boolean isConnected = false;
    private String tokens = null;

    private static DropboxAccount dropboxAccount = null;

    public static synchronized DropboxAccount getDropboxAccount() {
        if (dropboxAccount == null) {
            return new DropboxAccount();
        }
        return dropboxAccount;
    }

    public DropboxAccount() {
    }

    public void setTokens(String tokens) {
        this.tokens = tokens;
    }

    public String getTokens() {
        return tokens;
    }

    public void setUserInformation() throws DbxException {
        userInforation = new DropboxInformation(getUserClient(),
                new DropboxRequestConfig());
    }

    public DropboxInformation getUserInformation() {
        return userInforation;
    }

    public DbxClientV2 getUserClient() {
        if(tokens == null) {
            return null;
        }
        String access = "";
        try {
            access = AppCipher.decrytpTokens(tokens);
        } catch (NoSuchPaddingException | UnsupportedEncodingException
                | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException ex) {
            System.err.println(ex);
        } catch (Exception ex) {
            System.err.println(ex);
        }
        
        return new DbxClientV2(new DropboxRequestConfig(), access);
    }
    
    public static void setDropboxAccount(DropboxAccount dropboxAccount) {
        DropboxAccount.dropboxAccount = dropboxAccount;
    }
    
    public boolean isAccess() {
        return tokens != null && userInforation != null;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

}
