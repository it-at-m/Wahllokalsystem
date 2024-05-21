package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
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
class UrnenwahlvorbereitungValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    UrnenwahlvorbereitungValidator unitUnderTest;

    @Nested
    class ValidWahlbezirkIDOrThrow {

        @Test
        void noExceptionOnValidWahlbezirkID() {
            val idToValidate = "wahlbezirkID";

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validWahlbezirkIDOrThrow(idToValidate));
        }

        @Test
        void exceptionWhenWahlbezirkIDIsNull() {
            val exceptionToThrow = FachlicheWlsException.withCode("000").buildWithMessage("error");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SUCHKRITERIEN_UNVOLLSTAENDIG)).thenReturn(exceptionToThrow);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlbezirkIDOrThrow(null)).isSameAs(exceptionToThrow);
        }

        @Test
        void exceptionWhenWahlbezirkIDIsEmpty() {
            val exceptionToThrow = FachlicheWlsException.withCode("000").buildWithMessage("error");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SUCHKRITERIEN_UNVOLLSTAENDIG)).thenReturn(exceptionToThrow);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlbezirkIDOrThrow("")).isSameAs(exceptionToThrow);
        }
    }

}
