/**
 *
 */
package de.muenchen.oss.wahllokalsystem.wls.common.security;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptionBuilder {

    private static final Logger log = LoggerFactory.getLogger(EncryptionBuilder.class);
    private static ServiceIDFormatter formatter;

    private static final String AES = "AES";
    private final Cipher _encryptCipher;
    private final Cipher _decryptCipher;
    private static final String technischeExceptionKonstante = "S";

    public EncryptionBuilder(byte[] aSecret) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        val secret = new SecretKeySpec(aSecret, 0, 16, AES);
        formatter = new ServiceIDFormatter("tFormatter");
        _encryptCipher = Cipher.getInstance(AES);
        _encryptCipher.init(Cipher.ENCRYPT_MODE, secret);
        _decryptCipher = Cipher.getInstance(AES);
        _decryptCipher.init(Cipher.DECRYPT_MODE, secret);
    }

    public String decryptValue(String value) {
        if (value != null && !value.isEmpty()) {
            try {
                val decode = Base64.getUrlDecoder().decode(value.getBytes());
                val finalized = _decryptCipher.doFinal(decode);
                return new String(finalized);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                log.error("Unable to decrypt the given value <" + value + "> as of an " + e.getClass().getSimpleName() + ". Using direct object reference!", e);
                throw TechnischeWlsException.withCode(technischeExceptionKonstante).inService(formatter.getId())
                        .buildWithMessage("Problem bei Referenzierung/Dereferenzierung von Objekt-Referenzen");
            }
        }
        return value;
    }

    public String encryptValue(String value) {
        if (value != null && !value.isEmpty()) {
            try {
                val finalized = _encryptCipher.doFinal(value.getBytes());
                value = Base64.getUrlEncoder().encodeToString(finalized);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                log.error("Unable to encrypt the given value <" + value + "> as of an " + e.getClass().getSimpleName() + ". Using direct object reference!", e);
                throw TechnischeWlsException.withCode(technischeExceptionKonstante).inService(formatter.getId())
                        .buildWithMessage("Problem bei Referenzierung/Dereferenzierung von Objekt-Referenzen");
            }
        }
        return value;
    }
}
