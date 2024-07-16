package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.common.WahlurneModel;
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

    @Nested
    class ValidModelToSetOrThrow {

        @Test
        void noExceptionOnValidModel() {
            val validModel = initValid().build();

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validModelToSetOrThrow(validModel));
        }

        @Test
        void exceptionWhenModelIsNull() {
            val mockedFactoryException = FachlicheWlsException.withCode("000").buildWithMessage("error");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.PARAMS_UNVOLLSTAENDIG)).thenReturn(mockedFactoryException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validModelToSetOrThrow(null)).isSameAs(mockedFactoryException);
        }

        @Test
        void exceptionWhenWahlbezirkIDIsNull() {
            val invalidModel = initValid().wahlbezirkID(null).build();

            val mockedFactoryException = FachlicheWlsException.withCode("000").buildWithMessage("error");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.PARAMS_UNVOLLSTAENDIG)).thenReturn(mockedFactoryException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validModelToSetOrThrow(invalidModel)).isSameAs(mockedFactoryException);
        }

        @Test
        void exceptionWhenWahlbezirkIDIsEmpty() {
            val invalidModel = initValid().wahlbezirkID("").build();

            val mockedFactoryException = FachlicheWlsException.withCode("000").buildWithMessage("error");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.PARAMS_UNVOLLSTAENDIG)).thenReturn(mockedFactoryException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validModelToSetOrThrow(invalidModel)).isSameAs(mockedFactoryException);
        }

        @Test
        void exceptionWhenUrnenAnzahlIsNull() {
            val invalidModel = initValid().urnenAnzahl(null).build();

            val mockedFactoryException = FachlicheWlsException.withCode("000").buildWithMessage("error");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.PARAMS_UNVOLLSTAENDIG)).thenReturn(mockedFactoryException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validModelToSetOrThrow(invalidModel)).isSameAs(mockedFactoryException);
        }

        @Test
        void exceptionWhenUrnenAnzahlIsEmpty() {
            val invalidModel = initValid().urnenAnzahl(Collections.emptyList()).build();

            val mockedFactoryException = FachlicheWlsException.withCode("000").buildWithMessage("error");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.PARAMS_UNVOLLSTAENDIG)).thenReturn(mockedFactoryException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validModelToSetOrThrow(invalidModel)).isSameAs(mockedFactoryException);
        }

        private UrnenwahlvorbereitungModel.UrnenwahlvorbereitungModelBuilder initValid() {
            return UrnenwahlvorbereitungModel.builder().wahlbezirkID("wahlbezirkID").urnenAnzahl(List.of(new WahlurneModel("wahlID", 1, true)));
        }
    }

}
