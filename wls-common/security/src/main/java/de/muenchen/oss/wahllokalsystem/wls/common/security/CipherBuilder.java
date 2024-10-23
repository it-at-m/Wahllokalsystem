package de.muenchen.oss.wahllokalsystem.wls.common.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import lombok.val;

public class CipherBuilder {

    private static final String AES = "AES";

    public Cipher createEncryptionCipher(byte[] aSecret) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        val secret = new SecretKeySpec(aSecret, 0, 16, AES);
        val _encryptCipher = Cipher.getInstance(AES);
        _encryptCipher.init(Cipher.ENCRYPT_MODE, secret);
        return _encryptCipher;
    }

    public Cipher createDecryptionCipher(byte[] aSecret) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        val secret = new SecretKeySpec(aSecret, 0, 16, AES);
        val _decryptCipher = Cipher.getInstance(AES);
        _decryptCipher.init(Cipher.DECRYPT_MODE, secret);
        return _decryptCipher;
    }
}
