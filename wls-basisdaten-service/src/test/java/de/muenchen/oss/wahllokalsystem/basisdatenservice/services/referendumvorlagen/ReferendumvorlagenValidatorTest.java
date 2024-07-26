package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
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
class ReferendumvorlagenValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    ReferendumvorlagenValidator unitUnderTest;

    @Nested
    class ValidReferumvorlageReferenceModelOrThrow {

        private final FachlicheWlsException mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

        @Test
        void noExceptionWhenModelIsValid() {
            val validModel = initValidModel().build();

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validReferumvorlageReferenceModelOrThrow(validModel));
        }

        @Test
        void exceptionWhenModelIsNull() {
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETREFERENDUMVORLAGEN_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validReferumvorlageReferenceModelOrThrow(null)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenWahlIDIsNull() {
            val invalidModel = initValidModel().wahlID(null).build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETREFERENDUMVORLAGEN_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validReferumvorlageReferenceModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenWahlIDIsEmptyString() {
            val invalidModel = initValidModel().wahlID("").build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETREFERENDUMVORLAGEN_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validReferumvorlageReferenceModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenWahlbezirkIDIsNull() {
            val invalidModel = initValidModel().wahlbezirkID(null).build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETREFERENDUMVORLAGEN_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validReferumvorlageReferenceModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenWahlbezirkIDIsEmptyString() {
            val invalidModel = initValidModel().wahlbezirkID("").build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETREFERENDUMVORLAGEN_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validReferumvorlageReferenceModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        private ReferendumvorlagenReferenceModel.ReferendumvorlagenReferenceModelBuilder initValidModel() {
            return ReferendumvorlagenReferenceModel.builder().wahlbezirkID("wahlbezirkID").wahlID("wahlID");
        }

    }

}
