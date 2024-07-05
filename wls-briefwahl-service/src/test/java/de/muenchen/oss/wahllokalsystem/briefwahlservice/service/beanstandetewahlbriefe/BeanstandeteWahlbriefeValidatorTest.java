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

        private final String EXPECTED_CODE = "100";

        @Test
        void exceptionOnWahlbezirkIDIsNull() {
            val invalidReference = getValidReference().wahlbezirkID(null).build();

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideReferenceOrThrow(invalidReference)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionOnWahlbezirkIDIsEmptyString() {
            val invalidReference = getValidReference().wahlbezirkID("").build();
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideReferenceOrThrow(invalidReference)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionOnWahlbezirkIsBlankString() {
            val invalidReference = getValidReference().wahlbezirkID("   ").build();
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideReferenceOrThrow(invalidReference)).isSameAs(mockedWlsException);
        }

        @Test
        void excepionOnWaehlerverzeichnisnummerIsNull() {
            val invalidReference = getValidReference().waehlerverzeichnisNummer(null).build();
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideReferenceOrThrow(invalidReference)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionOnWaehlerverzeichnisnummerIsZero() {
            val invalidReference = getValidReference().waehlerverzeichnisNummer(0L).build();
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideReferenceOrThrow(invalidReference)).isSameAs(mockedWlsException);
        }

        @Test
        void noExceotionOnValideReference() {
            val valideReference = getValidReference().build();

            org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> unitUnderTest.valideReferenceOrThrow(valideReference));
        }

        private BeanstandeteWahlbriefeReference.BeanstandeteWahlbriefeReferenceBuilder getValidReference() {
            return BeanstandeteWahlbriefeReference.builder().wahlbezirkID("wbzId").waehlerverzeichnisNummer(1L);
        }

    }

    @Nested
    class ValideModelOrThrow {

        private final String EXPECTED_CODE = "101";

        @Test
        void exceptionOnBeanstandeteWahlbriefeIsNull() {
            val invalidModel = getValidModel().beanstandeteWahlbriefe(null).build();

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionOnWahlbezirkIDIsNull() {
            val invalidModel = getValidModel().wahlbezirkID(null).build();

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionOnWahlbezirkIDIsEmptyString() {
            val invalidModel = getValidModel().wahlbezirkID("").build();

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionOnWahlbezirkIDIsBlankString() {
            val invalidModel = getValidModel().wahlbezirkID("   ").build();

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionOnWaehlerverzeichnisNummerIsNull() {
            val invalidModel = getValidModel().waehlerverzeichnisNummer(null).build();

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionOnWaehlerverzeichnisNummerIsZero() {
            val invalidModel = getValidModel().waehlerverzeichnisNummer(0L).build();

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void noExceptionOnValideModel() {
            val modelToValidate = getValidModel().build();

            org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> unitUnderTest.valideModelOrThrow(modelToValidate));
        }

        private BeanstandeteWahlbriefeModel.BeanstandeteWahlbriefeModelBuilder getValidModel() {
            return BeanstandeteWahlbriefeModel.builder().beanstandeteWahlbriefe(new HashMap<>()).waehlerverzeichnisNummer(1L)
                    .wahlbezirkID("wbzId");
        }

    }

}
