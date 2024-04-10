/**
 *
 */
package de.muenchen.wls.common.security;

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
import com.fasterxml.jackson.databind.ObjectMapper;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.errorhandler.WlsResponseErrorHandler;

/**
 * Basisfunktionalität bezüglich symmetrischer Verschlüsselung.
 */
public class EncryptionBuilder {

    private static Logger log = LoggerFactory.getLogger(EncryptionBuilder.class);
    private static ServiceIDFormatter formatter = new ServiceIDFormatter("tFormatter");

    private ObjectMapper mapper = new ObjectMapper();

    private WlsResponseErrorHandler handler = new WlsResponseErrorHandler(mapper);

    private static final String AES = "AES";
    private Cipher _encryptCipher;
    private Cipher _decryptCipher;

    /**
     * Konstruktor für den HTTP-Filter.
     *
     * @throws InvalidKeyException wird geworfen bezüglich der
     *             AES-Verschlüsselung.
     * @throws NoSuchAlgorithmException wird geworfen bezüglich der
     *             AES-Verschlüsselung.
     * @throws NoSuchPaddingException wird geworfen bezüglich der
     *             AES-Verschlüsselung.
     */
    public EncryptionBuilder(byte[] aSecret) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        val secret = new SecretKeySpec(aSecret, 0, 16, AES);
        _encryptCipher = Cipher.getInstance(AES);
        _encryptCipher.init(Cipher.ENCRYPT_MODE, secret);
        _decryptCipher = Cipher.getInstance(AES);
        _decryptCipher.init(Cipher.DECRYPT_MODE, secret);
    }

    /**
     * Decrypts a single value. This is the hook where the actual decryption
     * mechanism is defined.
     *
     * @param value The value to be decrypted.
     * @return The decrypted value.
     */
    public String decryptValue(String value) {
        if (value != null && value.length() != 0) {
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

    /**
     * Encrypts a single value. This is the hook where the actual encryption
     * mechanism is defined.
     *
     * @param value The value to be encrypted.
     * @return The enscrypted value.
     */
    public String encryptValue(String value) {
        if (value != null && value.length() != 0) {
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
