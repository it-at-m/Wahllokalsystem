package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlschliessungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDateTime;
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
class UrnenwahlSchliessungsUhrzeitValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    UrnenwahlSchliessungsUhrzeitValidator unitUnderTest;

    @Nested
    class ValidWahlbezirkIDOrThrow {

        @Test
        void should_notThrowException_when_wahlbezirkIDIsValid() {
            val idToValidate = "wahlbezirkID";

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validWahlbezirkIDOrThrow(idToValidate));
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDisNull() {
            val exceptionToThrow = FachlicheWlsException.withCode("000").buildWithMessage("error");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SUCHKRITERIEN_UNVOLLSTAENDIG)).thenReturn(exceptionToThrow);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlbezirkIDOrThrow(null)).isSameAs(exceptionToThrow);
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDisEmpty() {
            val exceptionToThrow = FachlicheWlsException.withCode("000").buildWithMessage("error");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SUCHKRITERIEN_UNVOLLSTAENDIG)).thenReturn(exceptionToThrow);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlbezirkIDOrThrow("")).isSameAs(exceptionToThrow);
        }
    }

    @Nested
    class ValidModelToSetOrThrow {

        @Test
        void should_notThrowException_when_modelIsValid() {
            val validModel = initValid().build();

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validModelToSetOrThrow(validModel));
        }

        @Test
        void should_throwException_when_modelIsNull() {
            val mockedFactoryException = FachlicheWlsException.withCode("000").buildWithMessage("error");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.PARAMS_UNVOLLSTAENDIG)).thenReturn(mockedFactoryException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validModelToSetOrThrow(null)).isSameAs(mockedFactoryException);
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDisNull() {
            val invalidModel = initValid().wahlbezirkID(null).build();

            val mockedFactoryException = FachlicheWlsException.withCode("000").buildWithMessage("error");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.PARAMS_UNVOLLSTAENDIG)).thenReturn(mockedFactoryException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validModelToSetOrThrow(invalidModel)).isSameAs(mockedFactoryException);
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDisEmpty() {
            val invalidModel = initValid().wahlbezirkID("").build();

            val mockedFactoryException = FachlicheWlsException.withCode("000").buildWithMessage("error");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.PARAMS_UNVOLLSTAENDIG)).thenReturn(mockedFactoryException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validModelToSetOrThrow(invalidModel)).isSameAs(mockedFactoryException);
        }

        @Test
        void should_throwWlsException_when_urnenwahlSchliessungsuhrzeitIsNull() {
            val invalidModel = initValid().schliessungsuhrzeit(null).build();

            val mockedFactoryException = FachlicheWlsException.withCode("000").buildWithMessage("error");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.PARAMS_UNVOLLSTAENDIG)).thenReturn(mockedFactoryException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validModelToSetOrThrow(invalidModel)).isSameAs(mockedFactoryException);
        }

        private UrnenwahlSchliessungsUhrzeitModel.UrnenwahlSchliessungsUhrzeitModelBuilder initValid() {
            return UrnenwahlSchliessungsUhrzeitModel.builder().wahlbezirkID("wahlbezirkID").schliessungsuhrzeit(LocalDateTime.now());
        }
    }

}
