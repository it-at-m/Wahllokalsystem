package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.waehlerverzeichnis;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
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
class WaehlerverzeichnisValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    WaehlerverzeichnisValidator unitUnderTest;

    @Nested
    class ValidWaehlerverzeichnisReferenceOrThrow {

        @Test
        void should_notThrowException_when_referenceIsValid() {
            val validReference = initValid();

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validWaehlerverzeichnisReferenceOrThrow(validReference));
        }

        @Test
        void should_throwException_when_referenceIsNull() {
            val mockedException = FachlicheWlsException.withCode("000").inService("service").buildWithMessage("message");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SUCHKRITERIEN_UNVOLLSTAENDIG)).thenReturn(mockedException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWaehlerverzeichnisReferenceOrThrow(null)).isSameAs(mockedException);
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDisNull() {
            val invalidReference = initValid();
            invalidReference.setWahlbezirkID(null);

            val mockedException = FachlicheWlsException.withCode("000").inService("service").buildWithMessage("message");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SUCHKRITERIEN_UNVOLLSTAENDIG)).thenReturn(mockedException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWaehlerverzeichnisReferenceOrThrow(null)).isSameAs(mockedException);
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDisEmpty() {
            val invalidReference = initValid();
            invalidReference.setWahlbezirkID("");

            val mockedException = FachlicheWlsException.withCode("000").inService("service").buildWithMessage("message");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SUCHKRITERIEN_UNVOLLSTAENDIG)).thenReturn(mockedException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWaehlerverzeichnisReferenceOrThrow(null)).isSameAs(mockedException);
        }

        private BezirkIDUndWaehlerverzeichnisNummer initValid() {
            return new BezirkIDUndWaehlerverzeichnisNummer("wahlbezirkID", 12L);
        }
    }

    @Nested
    class ValidModelToSetOrThrow {

        @Test
        void should_notThrowException_when_modelIsValid() {
            val validModel = initValidModel().build();

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validModelToSetOrThrow(validModel));
        }

        @Test
        void should_throwException_when_modelIsNull() {
            val mockedException = FachlicheWlsException.withCode("000").inService("service").buildWithMessage("message");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.PARAMS_UNVOLLSTAENDIG)).thenReturn(mockedException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validModelToSetOrThrow(null)).isSameAs(mockedException);
        }

        @Test
        void should_throwWlsException_when_wahbezirkIDAndWaehlerverzeichnisNummerIsNull() {
            val invalidModel = initValidModel().waehlerverzeichnisReference(null).build();

            val mockedException = FachlicheWlsException.withCode("000").inService("service").buildWithMessage("message");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.PARAMS_UNVOLLSTAENDIG)).thenReturn(mockedException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validModelToSetOrThrow(invalidModel)).isSameAs(mockedException);
        }

        @Test
        void should_throwWlsException_when_waehlerverzeichnisNummerIsNull() {
            val invalidModel = initValidModel().build();
            invalidModel.waehlerverzeichnisReference().setWaehlerverzeichnisNummer(null);

            val mockedException = FachlicheWlsException.withCode("000").inService("service").buildWithMessage("message");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.PARAMS_UNVOLLSTAENDIG)).thenReturn(mockedException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validModelToSetOrThrow(invalidModel)).isSameAs(mockedException);
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDisNull() {
            val invalidModel = initValidModel().build();
            invalidModel.waehlerverzeichnisReference().setWahlbezirkID(null);

            val mockedException = FachlicheWlsException.withCode("000").inService("service").buildWithMessage("message");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.PARAMS_UNVOLLSTAENDIG)).thenReturn(mockedException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validModelToSetOrThrow(invalidModel)).isSameAs(mockedException);
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDisEmpty() {
            val invalidModel = initValidModel().build();
            invalidModel.waehlerverzeichnisReference().setWahlbezirkID("");

            val mockedException = FachlicheWlsException.withCode("000").inService("service").buildWithMessage("message");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.PARAMS_UNVOLLSTAENDIG)).thenReturn(mockedException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validModelToSetOrThrow(invalidModel)).isSameAs(mockedException);
        }

        private WaehlerverzeichnisModel.WaehlerverzeichnisModelBuilder initValidModel() {
            return WaehlerverzeichnisModel.builder().waehlerverzeichnisReference(new BezirkIDUndWaehlerverzeichnisNummer("wahlbezirkID", 89L));
        }
    }

}
