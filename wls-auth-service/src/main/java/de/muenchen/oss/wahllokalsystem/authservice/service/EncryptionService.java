package de.muenchen.oss.wahllokalsystem.authservice.service;

import de.muenchen.oss.wahllokalsystem.wls.common.security.EncryptionBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EncryptionService {

    private final EncryptionBuilder encryptionBuilder;

    @Value("${serviceauth.crypto.encryptionPrefix}")
    private String encryptedPrefix = "";

    public boolean isEncrypted(final String value) {
        return value.startsWith(encryptedPrefix);
    }

    public String encrypt(final String value) {
        return encryptedPrefix + encryptionBuilder.encryptValue(value);
    }

    public String decrypt(final String value) {
        if (value.startsWith(encryptedPrefix)) {
            val encryptedSubstring = value.substring(encryptedPrefix.length());
            return encryptionBuilder.decryptValue(encryptedSubstring);
        } else {
            log.warn("value was already decrypted");
            return value;
        }
    }
}
