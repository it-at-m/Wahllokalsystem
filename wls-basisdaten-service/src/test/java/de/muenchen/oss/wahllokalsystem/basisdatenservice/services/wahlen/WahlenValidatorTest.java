package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.Collections;
import java.util.List;
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
class WahlenValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    WahlenValidator unitUnderTest;

    @Nested
    class ValidWahlenCriteriaOrThrow {

        @Test
        void should_returnNoException_when_criteriaIsValid() {
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validWahlenCriteriaOrThrow("validWahltagID"));
        }

        @Test
        void should_returnException_when_criteriaIsNull() {
            val mockedException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETWAHLEN_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validWahlenCriteriaOrThrow(null)).isSameAs(mockedException);
        }

        @Test
        void should_returnException_when_criteriaIsEmpty() {
            val mockedException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETWAHLEN_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validWahlenCriteriaOrThrow("")).isSameAs(mockedException);
        }

        @Test
        void should_returnException_when_criteriaIsBlank() {
            val mockedException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETWAHLEN_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validWahlenCriteriaOrThrow("   ")).isSameAs(mockedException);
        }
    }

    @Nested
    class ValidWahlenWriteModelOrThrow {

        @Test
        void should_returnNoException_when_modelIsValid() {
            val validModel = new WahlenWriteModel("wahltagID", List.of(createEmptyWahlModel()));

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validWahlenWriteModelOrThrow(validModel));
        }

        @Test
        void should_returnException_when_wahltagIDIsNull() {
            val mockedException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTWAHLEN_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedException);

            val invalidModel = new WahlenWriteModel(null, List.of(createEmptyWahlModel()));

            Assertions.assertThatThrownBy(() -> unitUnderTest.validWahlenWriteModelOrThrow(invalidModel)).isSameAs(mockedException);
        }

        @Test
        void should_returnException_when_wahltagIDIsEmpty() {
            val mockedException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTWAHLEN_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedException);

            val invalidModel = new WahlenWriteModel("", List.of(createEmptyWahlModel()));

            Assertions.assertThatThrownBy(() -> unitUnderTest.validWahlenWriteModelOrThrow(invalidModel)).isSameAs(mockedException);
        }

        @Test
        void should_returnException_when_wahltagIDIsBlank() {
            val mockedException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTWAHLEN_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedException);

            val invalidModel = new WahlenWriteModel("   ", List.of(createEmptyWahlModel()));

            Assertions.assertThatThrownBy(() -> unitUnderTest.validWahlenWriteModelOrThrow(invalidModel)).isSameAs(mockedException);
        }

        @Test
        void should_returnException_when_wahlenIsNull() {
            val mockedException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTWAHLEN_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedException);

            val invalidModel = new WahlenWriteModel("wahltagID", null);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validWahlenWriteModelOrThrow(invalidModel)).isSameAs(mockedException);
        }

        @Test
        void should_returnException_when_wahlenIsEmpty() {
            val mockedException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTWAHLEN_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedException);

            val invalidModel = new WahlenWriteModel("wahltagID", Collections.emptyList());

            Assertions.assertThatThrownBy(() -> unitUnderTest.validWahlenWriteModelOrThrow(invalidModel)).isSameAs(mockedException);
        }

        private WahlModel createEmptyWahlModel() {
            return new WahlModel(null, null, null, null, null, null, null, null);
        }
    }

}
