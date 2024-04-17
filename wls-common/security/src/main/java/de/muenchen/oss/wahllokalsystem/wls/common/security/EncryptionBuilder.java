/**
 *
 */
package de.muenchen.oss.wahllokalsystem.wls.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.errorhandler.WlsResponseErrorHandler;
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
    private static final ServiceIDFormatter formatter = new ServiceIDFormatter("tFormatter");

    private final ObjectMapper mapper = new ObjectMapper();

    private final WlsResponseErrorHandler handler = new WlsResponseErrorHandler(mapper);

    private static final String AES = "AES";
    private final Cipher _encryptCipher;
    private final Cipher _decryptCipher;

    public EncryptionBuilder(byte[] aSecret) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        val secret = new SecretKeySpec(aSecret, 0, 16, AES);
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
                throw handler.createFalseObjectReferenceException(formatter.getId(), e);
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
                throw handler.createFalseObjectReferenceException(formatter.getId(), e);
            }
        }
        return value;
    }
}
