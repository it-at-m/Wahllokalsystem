package de.muenchen.oss.wahllokalsystem.briefwahlservice.service.beanstandetewahlbriefe;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.HashMap;
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
class BeanstandeteWahlbriefeValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    BeanstandeteWahlbriefeValidator unitUnderTest;

    @Nested
    class ValideReferenceOrThrow {

        @Test
        void should_throwWlsException_when_wahlbezirkIDIsNull() {
            val invalidReference = getValidReference().wahlbezirkID(null).build();

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideReferenceOrThrow(invalidReference)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDIsEmpty() {
            val invalidReference = getValidReference().wahlbezirkID("").build();
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideReferenceOrThrow(invalidReference)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDIsBlank() {
            val invalidReference = getValidReference().wahlbezirkID("   ").build();
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideReferenceOrThrow(invalidReference)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwWlsException_when_waehlerverzeichnisnummerIsNull() {
            val invalidReference = getValidReference().waehlerverzeichnisNummer(null).build();
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideReferenceOrThrow(invalidReference)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwWlsException_when_waehlerverzeichnisnummerIsZero() {
            val invalidReference = getValidReference().waehlerverzeichnisNummer(0L).build();
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideReferenceOrThrow(invalidReference)).isSameAs(mockedWlsException);
        }

        @Test
        void should_notThrowException_when_referenceIsValid() {
            val valideReference = getValidReference().build();

            org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> unitUnderTest.valideReferenceOrThrow(valideReference));
        }

        private BeanstandeteWahlbriefeReference.BeanstandeteWahlbriefeReferenceBuilder getValidReference() {
            return BeanstandeteWahlbriefeReference.builder().wahlbezirkID("wbzId").waehlerverzeichnisNummer(1L);
        }
    }

    @Nested
    class ValideModelOrThrow {

        @Test
        void should_throwWlsException_when_beanstandeteWahlbriefeIsNull() {
            val invalidModel = getValidModel().beanstandeteWahlbriefe(null).build();

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDIsNull() {
            val invalidModel = getValidModel().wahlbezirkID(null).build();

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDIsEmpty() {
            val invalidModel = getValidModel().wahlbezirkID("").build();

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDIsBlank() {
            val invalidModel = getValidModel().wahlbezirkID("   ").build();

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwWlsException_when_waehlerverzeichnisnummerIsNull() {
            val invalidModel = getValidModel().waehlerverzeichnisNummer(null).build();

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwWlsException_when_waehlerverzeichnisnummerIsZero() {
            val invalidModel = getValidModel().waehlerverzeichnisNummer(0L).build();

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void should_notThrowException_when_modelIsValid() {
            val modelToValidate = getValidModel().build();

            org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> unitUnderTest.valideModelOrThrow(modelToValidate));
        }

        private BeanstandeteWahlbriefeModel.BeanstandeteWahlbriefeModelBuilder getValidModel() {
            return BeanstandeteWahlbriefeModel.builder().beanstandeteWahlbriefe(new HashMap<>()).waehlerverzeichnisNummer(1L)
                    .wahlbezirkID("wbzId");
        }
    }
}
