package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
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
class KonfigurierterWahltagValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    KonfigurierterWahltagValidator unitUnderTest;

    @Nested
    class ValidPostModelOrThrow {

        @Test
        void noExceptionOnValidModel() {
            val validModel = initValidModel().build();

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validPostModelOrThrow(validModel));
        }

        @Test
        void exceptionWhenModelIsNull() {
            val expectedException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_KONFIGURIERTERWAHLTAG_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(expectedException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validPostModelOrThrow(null)).isSameAs(expectedException);
        }

        @Test
        void exceptionWhenWahltagIsNull() {
            val invalidModel = initValidModel().wahltag(null).build();

            val expectedException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_KONFIGURIERTERWAHLTAG_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(expectedException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validPostModelOrThrow(invalidModel)).isSameAs(expectedException);
        }

        @Test
        void exceptionWhenWahltagIDIsNull() {
            val invalidModel = initValidModel().wahltagID(null).build();

            val expectedException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_KONFIGURIERTERWAHLTAG_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(expectedException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validPostModelOrThrow(invalidModel)).isSameAs(expectedException);
        }
    }

    @Nested
    class ValidDeleteModelOrThrow {

        @Test
        void noExceptionOnValidModel() {
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validDeleteModelOrThrow("wahltagID"));
        }

        @Test
        void exceptionWhenWahltagIDIsNul() {
            val expectedException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.DELETE_KONFIGURIERTERWAHLTAG_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(expectedException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validDeleteModelOrThrow(null)).isSameAs(expectedException);
        }

        @Test
        void exceptionWhenWahltagIDIsEmpty() {
            val expectedException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.DELETE_KONFIGURIERTERWAHLTAG_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(expectedException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validDeleteModelOrThrow("")).isSameAs(expectedException);
        }
    }

    private KonfigurierterWahltagModel.KonfigurierterWahltagModelBuilder initValidModel() {
        return KonfigurierterWahltagModel.builder().wahltag(LocalDate.now()).wahltagID("wahltagID").active(true).nummer("nummer");
    }

}
