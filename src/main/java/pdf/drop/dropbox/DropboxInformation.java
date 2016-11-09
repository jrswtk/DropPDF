/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf.drop.dropbox;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;

import java.io.Serializable;

public class DropboxInformation implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String ident;
    private String name;
    private String email;
    private String country;
    private String userId;

    public DropboxInformation(DbxClientV2 client, DbxRequestConfig config) throws DbxException {
        this.ident = config.getClientIdentifier();
        this.name = getAccount(client).getName().getDisplayName();
        this.email = getAccount(client).getEmail();
        this.country = getAccount(client).getCountry();
        this.userId = getAccount(client).getAccountId();
    }

    public String getIdent() {
        return ident;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCountry() {
        return country;
    }

    public String getUserId() {
        return userId;
    }

    private FullAccount getAccount(DbxClientV2 client) throws DbxException {
        return client.users().getCurrentAccount();
    }

    @Override
    public String toString() {
        return "Dropbox Information: " +
                getName() + ", " +
                getEmail() + ", " +
                getCountry();
    }
    
}
