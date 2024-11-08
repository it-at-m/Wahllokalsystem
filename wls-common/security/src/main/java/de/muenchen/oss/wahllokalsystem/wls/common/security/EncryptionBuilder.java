/**
 *
 */
package de.muenchen.oss.wahllokalsystem.wls.common.security;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class EncryptionBuilder {

    private static final Logger log = LoggerFactory.getLogger(EncryptionBuilder.class);
    private static final String technischeExceptionKonstante = "399";

    private final ServiceIDFormatter formatter;
    private final Cipher encryptionCipher;
    private final Cipher decryptionCipher;

    public EncryptionBuilder(ServiceIDFormatter formatter,
            @Qualifier("encryptionCipher") Cipher encryptionCipher,
            @Qualifier("decryptionCipher") Cipher decryptionCipher) {
        this.formatter = formatter;
        this.encryptionCipher = encryptionCipher;
        this.decryptionCipher = decryptionCipher;
    }

    public String decryptValue(String value) {
        if (value != null && !value.isEmpty()) {
            try {
                val decode = Base64.getUrlDecoder().decode(value.getBytes());
                val finalized = decryptionCipher.doFinal(decode);
                return new String(finalized);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                log.error("Unable to decrypt the given value <" + value + "> as of an " + e.getClass().getSimpleName() + ". Using direct object reference!", e);
                throw TechnischeWlsException.withCode(technischeExceptionKonstante).inService(formatter.getId())
                        .buildWithMessage(
                                "Exception" + " " + technischeExceptionKonstante + ":Problem bei Referenzierung/Dereferenzierung von Objekt-Referenzen");
            }
        }
        return value;
    }

    public String encryptValue(String value) {
        if (value != null && !value.isEmpty()) {
            try {
                val finalized = encryptionCipher.doFinal(value.getBytes());
                value = Base64.getUrlEncoder().encodeToString(finalized);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                log.error("Unable to encrypt the given value <" + value + "> as of an " + e.getClass().getSimpleName() + ". Using direct object reference!", e);
                throw TechnischeWlsException.withCode(technischeExceptionKonstante).inService(formatter.getId())
                        .buildWithMessage(
                                "Exception" + " " + technischeExceptionKonstante + ":Problem bei Referenzierung/Dereferenzierung von Objekt-Referenzen");
            }
        }
        return value;
    }
}
