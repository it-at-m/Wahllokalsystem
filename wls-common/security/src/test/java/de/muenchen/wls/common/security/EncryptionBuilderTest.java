package de.muenchen.wls.common.security;

import de.muenchen.wls.common.security.testultils.LoggerExtension;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

class EncryptionBuilderTest {

    @RegisterExtension
    public LoggerExtension loggerExtension = new LoggerExtension();

    @Nested
    class decrypt {

        @Test
        void sucessful() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
            val aByte = new byte[16];
            val unitUnderTest = new EncryptionBuilder(aByte);
            Assertions.assertThat(unitUnderTest.decryptValue("Efl8HLaoqguJ-CkS4r_m_QFD22PuZrDN_59pkXaAFR4=")).isEqualTo("376526723AFDAB3D");
        }

        @Test
        void throwBadPadding() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
            val aByte = new byte[16];
            val random = new SecureRandom();
            random.nextBytes(aByte);
            val unitUnderTest = new EncryptionBuilder(aByte);
            try {
                unitUnderTest.decryptValue("Efl8HLaoqguJ-CkS4r_m_QFD22PuZrDN_59pkXaAFR4=");
            } catch (Exception e) {
                Assertions.assertThat(loggerExtension.getFormattedMessages().size()).isEqualTo(1);
            }

        }
    }

    @Nested
    class encrypt {

        @Test
        void successful() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
            val aByte = new byte[16];
            val unitUnderTest = new EncryptionBuilder(aByte);
            Assertions.assertThat(unitUnderTest.encryptValue("376526723AFDAB3D")).isEqualTo("Efl8HLaoqguJ-CkS4r_m_QFD22PuZrDN_59pkXaAFR4=");
        }

        @Test
        void throwBadPadding() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
            val aByte = new byte[16];
            val random = new SecureRandom();
            random.nextBytes(aByte);
            val unitUnderTest = new EncryptionBuilder(aByte);
            try {
                unitUnderTest.encryptValue("376526723AFDAB3D");
            } catch (Exception e) {
                Assertions.assertThat(loggerExtension.getFormattedMessages().size()).isEqualTo(1);
            }
        }

    }
}
