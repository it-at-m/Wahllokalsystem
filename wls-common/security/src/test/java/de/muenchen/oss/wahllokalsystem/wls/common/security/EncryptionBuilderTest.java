package de.muenchen.oss.wahllokalsystem.wls.common.security;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.LoggerExtension;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EncryptionBuilderTest {

    @RegisterExtension
    public LoggerExtension loggerExtension = new LoggerExtension();

    @Mock
    Cipher cipher;

    @Mock
    ServiceIDFormatter formatter;

    @InjectMocks
    EncryptionBuilder unitUnderTest;

    @Nested
    class DecryptValue {

        @Test
        void successful() throws IllegalBlockSizeException, BadPaddingException {
            Mockito.when(cipher.doFinal("376526723AFDAB3D".getBytes())).thenReturn("mockedText".getBytes());
            Assertions.assertThat(unitUnderTest.decryptValue("Mzc2NTI2NzIzQUZEQUIzRA==")).isEqualTo("mockedText");
        }

        @Test
        void emptyValue() {
            Assertions.assertThat(unitUnderTest.decryptValue("")).isEmpty();
        }

        @Test
        void valueIsNull() {
            Assertions.assertThat(unitUnderTest.decryptValue(null)).isNull();
        }

        @Test
        void throwBadPadding() throws IllegalBlockSizeException, BadPaddingException {
            val mockedBadPaddingException = new BadPaddingException("MockedBadPadding");
            Mockito.when(formatter.getId()).thenReturn("1");
            Mockito.when(cipher.doFinal("376526723AFDAB3D".getBytes())).thenThrow(mockedBadPaddingException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.decryptValue("Mzc2NTI2NzIzQUZEQUIzRA=="))
                    .isInstanceOf(TechnischeWlsException.class);
        }

        @Test
        void throwIllegalBlockSize() throws IllegalBlockSizeException, BadPaddingException {
            val mockedIllegalBlockSizeException = new IllegalBlockSizeException("MockedIllegalBlockSize");
            Mockito.when(formatter.getId()).thenReturn("1");
            Mockito.when(cipher.doFinal("376526723AFDAB3D".getBytes())).thenThrow(mockedIllegalBlockSizeException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.decryptValue("Mzc2NTI2NzIzQUZEQUIzRA=="))
                    .isInstanceOf(TechnischeWlsException.class);
        }

        @Test
        void correctExceptionCodeIsThrown() throws IllegalBlockSizeException, BadPaddingException {
            val mockedIllegalBlockSizeException = new IllegalBlockSizeException("MockedIllegalBlockSize");
            Mockito.when(formatter.getId()).thenReturn("1");
            Mockito.when(cipher.doFinal("376526723AFDAB3D".getBytes())).thenThrow(mockedIllegalBlockSizeException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.decryptValue("Mzc2NTI2NzIzQUZEQUIzRA=="))
                    .hasFieldOrPropertyWithValue("code", "399");
        }
    }

    @Nested
    class EncryptValue {

        @Test
        void successful() throws Exception {
            Mockito.when(cipher.doFinal("376526723AFDAB3D".getBytes())).thenReturn("secret".getBytes());
            Assertions.assertThat(unitUnderTest.encryptValue("376526723AFDAB3D")).isEqualTo("c2VjcmV0");
        }

        @Test
        void emptyValue() {
            Assertions.assertThat(unitUnderTest.encryptValue("")).isEmpty();
        }

        @Test
        void valueIsNull() {
            Assertions.assertThat(unitUnderTest.encryptValue(null)).isNull();
        }

        @Test
        void throwBadPadding() throws IllegalBlockSizeException, BadPaddingException {
            val mockedBadPaddingException = new BadPaddingException("MockedBadPadding");
            Mockito.when(formatter.getId()).thenReturn("1");
            Mockito.when(cipher.doFinal("Mzc2NTI2NzIzQUZEQUIzRA==".getBytes())).thenThrow(mockedBadPaddingException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.encryptValue("Mzc2NTI2NzIzQUZEQUIzRA=="))
                    .isInstanceOf(TechnischeWlsException.class);
        }

        @Test
        void throwIllegalBlockSize() throws IllegalBlockSizeException, BadPaddingException {
            val mockedIllegalBlockSizeException = new IllegalBlockSizeException("MockedIllegalBlockSize");
            Mockito.when(formatter.getId()).thenReturn("1");
            Mockito.when(cipher.doFinal("Mzc2NTI2NzIzQUZEQUIzRA==".getBytes())).thenThrow(mockedIllegalBlockSizeException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.encryptValue("Mzc2NTI2NzIzQUZEQUIzRA=="))
                    .isInstanceOf(TechnischeWlsException.class);
        }

        @Test
        void correctExceptionCodeIsThrown() throws IllegalBlockSizeException, BadPaddingException {
            val mockedIllegalBlockSizeException = new IllegalBlockSizeException("MockedIllegalBlockSize");

            Mockito.when(formatter.getId()).thenReturn("1");
            Mockito.when(cipher.doFinal("Mzc2NTI2NzIzQUZEQUIzRA==".getBytes())).thenThrow(mockedIllegalBlockSizeException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.encryptValue("Mzc2NTI2NzIzQUZEQUIzRA=="))
                    .hasFieldOrPropertyWithValue("code", "399");
        }
    }
}
