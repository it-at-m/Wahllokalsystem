package de.muenchen.wls.common.wls.security;

import de.muenchen.wls.common.security.EncryptionBuilder;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HexFormat;
@ExtendWith(MockitoExtension.class)
class EncryptionBuilderTest {

    byte[] aByte = HexFormat.of().parseHex("d1299fe9384daf3d82dd3f");
    @Mock
    private EncryptionBuilder mockedCreator;

    @Test
    void decrypt() {
        val unitUnderTest = new EncryptionBuilder(mockedCreator);
        unitUnderTest.decryptValue("ZDEyOTlmZTkzODRkYWYzZDgyZGQzZg==").contains("d1299fe9384daf3d82dd3f");
    }

    @Test
    void encrypt() {
        val unitUnderTest = new EncryptionBuilder(mockedCreator);
        unitUnderTest.encryptValue("d1299fe9384daf3d82dd3f").contains("ZDEyOTlmZTkzODRkYWYzZDgyZGQzZg==");
    }
}
