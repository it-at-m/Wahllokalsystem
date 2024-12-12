package de.muenchen.oss.wahllokalsystem.authservice.service;

import de.muenchen.oss.wahllokalsystem.authservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Getter
@Setter
public class CryptoService {

    private final ServiceIDFormatter formatter;
    private final Cipher encryptionCipher;
    private final Cipher decryptionCipher;

    @Value("${service.config.crypto.encryptionPrefix}")
    private String encryptedPrefix = "";

    public CryptoService(ServiceIDFormatter formatter,
            @Qualifier("encryptionCipher") Cipher encryptionCipher,
            @Qualifier("decryptionCipher") Cipher decryptionCipher) {
        this.formatter = formatter;
        this.encryptionCipher = encryptionCipher;
        this.decryptionCipher = decryptionCipher;
    }

    public boolean isEncrypted(final String value) {
        return value.startsWith(encryptedPrefix);
    }

    public String encrypt(final String value) {
        return encryptedPrefix + encryptValue(value);
    }

    public String decrypt(final String value) {
        if (isEncrypted(value)) {
            val encryptedSubstring = value.substring(encryptedPrefix.length());
            return decryptValue(encryptedSubstring);
        } else {
            log.warn("value was already decrypted");
            return value;
        }
    }

    private String decryptValue(String value) {
        if (value != null && !value.isEmpty()) {
            try {
                val decode = Base64.getUrlDecoder().decode(value.getBytes());
                val finalized = decryptionCipher.doFinal(decode);
                return new String(finalized);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                log.error("Unable to decrypt the value due to " + e.getClass().getSimpleName() + ". Using direct object reference!", e);
                throw TechnischeWlsException.withCode(ExceptionConstants.CRYPTO_EXCEPTION_CODE).inService(formatter.getId())
                        .buildWithMessage("Problem bei der Entschlüsselung von Objekt-Referenzen");
            }
        }
        return value;
    }

    private String encryptValue(String value) {
        if (value != null && !value.isEmpty()) {
            try {
                val finalized = encryptionCipher.doFinal(value.getBytes());
                value = Base64.getUrlEncoder().encodeToString(finalized);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                log.error("Unable to encrypt the value due to " + e.getClass().getSimpleName() + ". Using direct object reference!", e);
                throw TechnischeWlsException.withCode(ExceptionConstants.CRYPTO_EXCEPTION_CODE).inService(formatter.getId())
                        .buildWithMessage("Problem bei der Verschlüsselung von Objekt-Referenzen");
            }
        }
        return value;
    }
}
