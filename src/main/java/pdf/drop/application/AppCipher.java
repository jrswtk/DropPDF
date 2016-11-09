/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf.drop.application;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class AppCipher implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final String CIPHER = "asdSDDcnzZpotrEK";

    public static String decrytpTokens(String tokens) throws NoSuchAlgorithmException,
            NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, Exception {

        Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
        byte[] plainBytes = cipher.doFinal(Base64.decode(tokens));

        return new String(plainBytes);
    }

    public static String encrytpTokens(String tokens) throws NoSuchAlgorithmException,
            NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, Base64DecodingException,
            Exception {

        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
        byte[] encryptedBytes = cipher.doFinal(tokens.getBytes());

        return Base64.encode(encryptedBytes);
    }

    private static Cipher getCipher(int cipherMode) throws Exception {
        String encryptionAlgorithm = "AES";
		
        SecretKeySpec keySpecification = new SecretKeySpec(
                AppCipher.CIPHER.getBytes("UTF-8"), "AES");
				
        Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
        cipher.init(cipherMode, keySpecification);

        return cipher;
    }
    
}
