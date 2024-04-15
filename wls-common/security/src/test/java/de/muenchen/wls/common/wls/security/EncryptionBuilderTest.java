package de.muenchen.wls.common.wls.security;

import de.muenchen.wls.common.security.EncryptionBuilder;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

class EncryptionBuilderTest {
    byte[] toBeDecrypted = "Mzc2NTI2NzIzQUZEQUIzRD==".getBytes();
    byte [] toBeEncrypted = "376526723AFDAB3D".getBytes();

    @Test
    void decrypt() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        val aByte = new byte[16];
        val secureRandom = new SecureRandom();
        secureRandom.nextBytes(aByte);
        val unitUnderTest = new EncryptionBuilder(aByte);
        Assertions.assertThat(unitUnderTest.decryptValue("Mzc2NTI2NzIzQUZEQUIzRD==").contains((Base64.getUrlDecoder().decode(toBeDecrypted).toString())));
    }

    @Test
    void encrypt() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        val aByte = new byte[16];
        val secureRandom = new SecureRandom();
        secureRandom.nextBytes(aByte);
        val unitUnderTest = new EncryptionBuilder(aByte);
        Assertions.assertThat(unitUnderTest.encryptValue("376526723AFDAB3D").contains(Base64.getUrlEncoder().encode(toBeEncrypted).toString()));
    }
}
