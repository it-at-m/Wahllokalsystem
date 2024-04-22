package de.muenchen.oss.wahllokalsystem.wls.common.security;

import static org.assertj.core.api.Assertions.fail;
import de.muenchen.oss.wahllokalsystem.wls.common.security.testultils.LoggerExtension;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.NoSuchPaddingException;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

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
        void emptyValue() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
            val aByte = new byte[16];
            val unitUnderTest = new EncryptionBuilder(aByte);
            Assertions.assertThat(unitUnderTest.decryptValue("").isEmpty()).isTrue();
        }

        @Test
        void valueIsNull() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
            val aByte = new byte[16];
            val unitUnderTest = new EncryptionBuilder(aByte);
            Assertions.assertThat(unitUnderTest.decryptValue(null) == null).isTrue();
        }

        @Test
        void throwBadPadding() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
            val aByte = new byte[16];
            val random = new SecureRandom();
            random.nextBytes(aByte);
            val unitUnderTest = new EncryptionBuilder(aByte);
            try {
                unitUnderTest.decryptValue("Efl8HLaoqguJ-CkS4r_m_QFD22PuZrDN_59pkXaAFR4=");
                fail("Exception not thrown");
            } catch (Exception e) {
                Assertions.assertThat(e.getMessage().contains("Problem bei Referenzierung/Dereferenzierung von Objekt-Referenzen")).isTrue();
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
        void emptyValue() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
            val aByte = new byte[16];
            val unitUnderTest = new EncryptionBuilder(aByte);
            Assertions.assertThat(unitUnderTest.encryptValue("").isEmpty()).isTrue();
        }

        @Test
        void valueIsNull() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
            val aByte = new byte[16];
            val unitUnderTest = new EncryptionBuilder(aByte);
            Assertions.assertThat(unitUnderTest.encryptValue(null) == null).isTrue();
        }
    }
}
