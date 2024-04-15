package de.muenchen.wls.common.wls.security;

import de.muenchen.wls.common.security.EncryptionBuilder;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;


@ExtendWith(MockitoExtension.class)
class EncryptionBuilderTest {
    byte[] toBeDecrypted = "Mzc2NTI2NzIzQUZEQUIzRD==".getBytes();
    byte [] toBeEncrypted = "376526723AFDAB3D".getBytes();

    @Mock
    private EncryptionBuilder mockedCreator;

    @Test
    void decrypt() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Mockito.when(mockedCreator.decryptValue("Mzc2NTI2NzIzQUZEQUIzRD==")).thenReturn("376526723AFDAB3D");
        val aByte = new byte[16];
        val secureRandom = new SecureRandom();
        secureRandom.nextBytes(aByte);
        val unitUnderTest = new EncryptionBuilder(aByte);
        Assertions.assertThat(unitUnderTest.decryptValue("Mzc2NTI2NzIzQUZEQUIzRD==").contains((Arrays.toString(Base64.getUrlDecoder().decode(toBeDecrypted))))).isTrue();
    }

    @Test
    void encrypt() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Mockito.when(mockedCreator.encryptValue("376526723AFDAB3D")).thenReturn("Mzc2NTI2NzIzQUZEQUIzRD==");
        val aByte = new byte[16];
        val secureRandom = new SecureRandom();
        secureRandom.nextBytes(aByte);
        val unitUnderTest = new EncryptionBuilder(aByte);
        Assertions.assertThat(unitUnderTest.encryptValue("376526723AFDAB3D").contains(Arrays.toString(Base64.getUrlEncoder().encode(toBeEncrypted)))).isTrue();
    }
}
