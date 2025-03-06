package de.muenchen.oss.wahllokalsystem.authservice.service;

import de.muenchen.oss.wahllokalsystem.authservice.common.CryptoService;
import de.muenchen.oss.wahllokalsystem.authservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Stream;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CryptoServiceTest {

    private static final String ENCRYPTION_PREFIX = "encryptionPrefix";

    @Mock
    ServiceIDFormatter idFormatter;

    @Mock
    Cipher cipher;

    @InjectMocks
    CryptoService unitUnderTest;

    @BeforeEach
    void setUp() {
        unitUnderTest.setEncryptedPrefix(ENCRYPTION_PREFIX);
    }

    @Nested
    class IsEncrypted {

        @Test
        void should_returnTrue_when_valueStartsWithPrefix() {
            Assertions.assertThat(unitUnderTest.isEncrypted(ENCRYPTION_PREFIX + "the encrypted value")).isTrue();
        }

        @Test
        void should_returnFalse_when_valueDoesNotStartWithPrefix() {
            unitUnderTest.setEncryptedPrefix("prefix");
            Assertions.assertThat(unitUnderTest.isEncrypted("values without encryption prefix")).isFalse();
        }
    }

    @Nested
    class Encrypt {

        @Test
        void should_returnEncryptedValueWithPrefix_when_valueIsGiven() throws Exception {
            val valueToEncrypt = "hello world";

            val mockedEncryptedValue = "encrypted value".getBytes();
            Mockito.when(cipher.doFinal(valueToEncrypt.getBytes())).thenReturn(mockedEncryptedValue);

            val result = unitUnderTest.encrypt(valueToEncrypt);

            val expectedResult = ENCRYPTION_PREFIX + Base64.getEncoder().encodeToString(mockedEncryptedValue);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void should_returnEncryptionPrefix_when_emptyStringValueIsGiven() {
            Assertions.assertThat(unitUnderTest.encrypt("")).isEqualTo(ENCRYPTION_PREFIX);
        }

        @Test
        void should_returnEncryptionPrefix_when_nullIsGiven() {
            Assertions.assertThat(unitUnderTest.encrypt(null)).isEqualTo(ENCRYPTION_PREFIX + null);
        }

        @ParameterizedTest
        @MethodSource("de.muenchen.oss.wahllokalsystem.authservice.service.CryptoServiceTest#exceptionsMappedToWlsException")
        void should_throwTechnischeWlsException_when_cipherThrowsException(final Exception exceptionThrownByCipher) throws Exception {
            val valueToEncrypt = "hello world";

            val mockedServiceID = "authService";
            Mockito.when(idFormatter.getId()).thenReturn(mockedServiceID);
            Mockito.doThrow(exceptionThrownByCipher).when(cipher).doFinal(valueToEncrypt.getBytes());

            val expectedException = TechnischeWlsException.withCode(ExceptionConstants.CRYPTO_EXCEPTION_CODE).inService(mockedServiceID)
                    .buildWithMessage("");

            Assertions.assertThatThrownBy(() -> unitUnderTest.encrypt(valueToEncrypt))
                    .satisfies(exception -> {
                        Assertions.assertThat(exception)
                                .usingRecursiveComparison()
                                .ignoringFields("message")
                                .isEqualTo(expectedException);
                        Assertions.assertThat(exception).hasNoNullFieldsOrProperties();
                    });
        }
    }

    @Nested
    class Decrypt {

        @Test
        void should_returnValue_when_valueIsNotEncrypted() {
            val notEncryptedValue = Base64.getEncoder().encodeToString("not encrypted value".getBytes());
            Assertions.assertThat(unitUnderTest.decrypt(notEncryptedValue)).isEqualTo(notEncryptedValue);
        }

        @Test
        void should_returnDecryptedValue_when_valueIsGiven() throws Exception {
            val encryptedValue = "the encrypted value";
            val encryptedValueAsBase64WithPrefix = ENCRYPTION_PREFIX + Base64.getEncoder().encodeToString(encryptedValue.getBytes());

            val mockDecrypted = "decrypted value";
            Mockito.when(cipher.doFinal(encryptedValue.getBytes())).thenReturn(mockDecrypted.getBytes());

            val result = unitUnderTest.decrypt(encryptedValueAsBase64WithPrefix);

            Assertions.assertThat(result).isEqualTo(mockDecrypted);
        }

        @ParameterizedTest
        @MethodSource("de.muenchen.oss.wahllokalsystem.authservice.service.CryptoServiceTest#exceptionsMappedToWlsException")
        void should_throwTechnischeWlsException_when_cipherThrowsException(final Exception exceptionThrownByCipher) throws Exception {
            val encryptedValue = "the encrypted value";
            val encryptedValueAsBase64WithPrefix = ENCRYPTION_PREFIX + Base64.getEncoder().encodeToString(encryptedValue.getBytes());

            val mockedServiceID = "authService";
            Mockito.when(idFormatter.getId()).thenReturn(mockedServiceID);
            Mockito.doThrow(exceptionThrownByCipher).when(cipher).doFinal(encryptedValue.getBytes());

            val expectedException = TechnischeWlsException.withCode(ExceptionConstants.CRYPTO_EXCEPTION_CODE).inService(mockedServiceID)
                    .buildWithMessage("");

            Assertions.assertThatThrownBy(() -> unitUnderTest.decrypt(encryptedValueAsBase64WithPrefix))
                    .satisfies(exception -> {
                        Assertions.assertThat(exception)
                                .usingRecursiveComparison()
                                .ignoringFields("message")
                                .isEqualTo(expectedException);
                        Assertions.assertThat(exception).hasNoNullFieldsOrProperties();
                    });
        }
    }

    public static Stream<Arguments> exceptionsMappedToWlsException() {
        val exceptions = Set.of(new IllegalBlockSizeException(), new BadPaddingException());

        return exceptions.stream().map(exception -> Arguments.of(exception, exception.getClass().getName()));
    }
}
