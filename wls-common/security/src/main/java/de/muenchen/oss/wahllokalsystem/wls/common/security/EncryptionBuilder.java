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
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptionBuilder {

    private static final Logger log = LoggerFactory.getLogger(EncryptionBuilder.class);
    private static ServiceIDFormatter formatter;
    private static final String technischeExceptionKonstante = "S";

    private final byte[] aSecretFinal;

    public EncryptionBuilder(byte[] aSecretfinal, ServiceIDFormatter formatter) {
        EncryptionBuilder.formatter = formatter;
        this.aSecretFinal = aSecretfinal;
    }

    public String decryptValue(String value) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        CipherBuilder cipherBuilder = new CipherBuilder();
        if (value != null && !value.isEmpty()) {
            try {
                val decode = Base64.getUrlDecoder().decode(value.getBytes());
                val finalized = cipherBuilder.createDecryptionCipher(aSecretFinal).doFinal(decode);
                return new String(finalized);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                log.error("Unable to decrypt the given value <" + value + "> as of an " + e.getClass().getSimpleName() + ". Using direct object reference!", e);
                throw TechnischeWlsException.withCode(technischeExceptionKonstante).inService(formatter.getId())
                        .buildWithMessage("Problem bei Referenzierung/Dereferenzierung von Objekt-Referenzen");
            }
        }
        return value;
    }

    public String encryptValue(String value) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        CipherBuilder cipherBuilder = new CipherBuilder();
        if (value != null && !value.isEmpty()) {
            try {
                val finalized = cipherBuilder.createEncryptionCipher(aSecretFinal).doFinal(value.getBytes());
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
