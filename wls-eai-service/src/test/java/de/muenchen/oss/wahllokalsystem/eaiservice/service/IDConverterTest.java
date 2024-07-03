package de.muenchen.oss.wahllokalsystem.eaiservice.service;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IDConverterTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    IDConverter unitUnderTest;

    @Nested
    class ConvertIDToUUIDOrThrow {

        @Test
        void stringIsConverted() {
            val idToConvert = "7db3ebc6-d2f9-4b7d-a703-6d1677f3f305";

            val result = unitUnderTest.convertIDToUUIDOrThrow(idToConvert);

            Assertions.assertThat(result.toString()).isEqualTo(idToConvert);
        }

        @Test
        void wlsExceptionWhenNotConvertable() {
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.ID_NICHT_KONVERTIERBAR)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.convertIDToUUIDOrThrow("")).isSameAs(mockedWlsException);
        }
    }

}
