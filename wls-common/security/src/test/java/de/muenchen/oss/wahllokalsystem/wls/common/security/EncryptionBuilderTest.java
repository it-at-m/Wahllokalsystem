package de.muenchen.oss.wahllokalsystem.wls.common.security;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import de.muenchen.oss.wahllokalsystem.wls.common.security.testultils.LoggerExtension;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.NoSuchPaddingException;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

class EncryptionBuilderTest {

    @RegisterExtension
    public LoggerExtension loggerExtension = new LoggerExtension();

    @Mock
    ServiceIDFormatter formatter;

    @Nested
    @ExtendWith(MockitoExtension.class)
    class DecryptValue {

        @Test
        void sucessful() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
            val aByte = new byte[16];
            val unitUnderTest = new EncryptionBuilder(aByte, formatter);
            Assertions.assertThat(unitUnderTest.decryptValue("Efl8HLaoqguJ-CkS4r_m_QFD22PuZrDN_59pkXaAFR4=")).isEqualTo("376526723AFDAB3D");
        }

        @Test
        void emptyValue() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
            val aByte = new byte[16];
            val unitUnderTest = new EncryptionBuilder(aByte, formatter);
            Assertions.assertThat(unitUnderTest.decryptValue("")).isEmpty();
        }

        @Test
        void valueIsNull() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
            val aByte = new byte[16];
            val unitUnderTest = new EncryptionBuilder(aByte, formatter);
            Assertions.assertThat(unitUnderTest.decryptValue(null)).isNull();
        }

        @Test
        void throwBadPadding() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
            val aByte = new byte[16];
            val random = new SecureRandom();
            random.nextBytes(aByte);
            val unitUnderTest = new EncryptionBuilder(aByte, formatter);
            Mockito.when(formatter.getId()).thenReturn("1234");
            Assertions.assertThatThrownBy(() -> unitUnderTest.decryptValue("Efl8HLaoqguJ-CkS4r_m_QFD22PuZrDN_59pkXaAFR4=")).isExactlyInstanceOf(
                    TechnischeWlsException.class).hasMessageContaining("Problem bei Referenzierung/Dereferenzierung von Objekt-Referenzen");
            Assertions.assertThat(loggerExtension.getFormattedMessages().size()).isEqualTo(1);
        }
    }

    @Nested
    class EncryptValue {

        @Test
        void successful() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
            val aByte = new byte[16];
            val unitUnderTest = new EncryptionBuilder(aByte, formatter);
            Assertions.assertThat(unitUnderTest.encryptValue("376526723AFDAB3D")).isEqualTo("Efl8HLaoqguJ-CkS4r_m_QFD22PuZrDN_59pkXaAFR4=");
        }

        @Test
        void emptyValue() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
            val aByte = new byte[16];
            val unitUnderTest = new EncryptionBuilder(aByte, formatter);
            Assertions.assertThat(unitUnderTest.encryptValue("")).isEmpty();
        }

        @Test
        void valueIsNull() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
            val aByte = new byte[16];
            val unitUnderTest = new EncryptionBuilder(aByte, formatter);
            Assertions.assertThat(unitUnderTest.encryptValue(null)).isNull();
        }
    }
}
