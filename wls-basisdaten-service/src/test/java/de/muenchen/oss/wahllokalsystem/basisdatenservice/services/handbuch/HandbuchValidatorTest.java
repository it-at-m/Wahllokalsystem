package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch;

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
class HandbuchValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    HandbuchValidator unitUnderTest;

    @Nested
    class ValidHandbuchReferenceOrThrow {

        private final FachlicheWlsException mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

        @Test
        void noExceptionWhenModelIsValid() {
            val validModel = initValidModel().build();

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validHandbuchReferenceOrThrow(validModel));
        }

        @Test
        void exceptionWhenModelIsNull() {
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETHANDBUCH_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validHandbuchReferenceOrThrow(null)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenWahltagIDIsNull() {
            val invalidModel = initValidModel().wahltagID(null).build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETHANDBUCH_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validHandbuchReferenceOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenWahltagIDIsEmptyString() {
            val invalidModel = initValidModel().wahltagID("").build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETHANDBUCH_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validHandbuchReferenceOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenWahltagIDIsBlankString() {
            val invalidModel = initValidModel().wahltagID("    ").build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETHANDBUCH_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validHandbuchReferenceOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenWahlbezirksArtIsNull() {
            val invalidModel = initValidModel().wahlbezirksart(null).build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETHANDBUCH_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validHandbuchReferenceOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        private HandbuchReferenceModel.HandbuchReferenceModelBuilder initValidModel() {
            return HandbuchReferenceModel.builder().wahlbezirksart(WahlbezirkArtModel.BWB).wahltagID("wahltagID");
        }
    }

    @Nested
    class ValidHandbuchWriteModelOrThrow {

        private final FachlicheWlsException mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

        @Test
        void noExceptionWhenModelIsValid() {
            val validModel = initValidModel().build();

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validHandbuchWriteModelOrThrow(validModel));
        }

        @Test
        void exceptionWhenModelIsNull() {
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTHANDBUCH_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validHandbuchWriteModelOrThrow(null)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenHandbuchReferenceIsNull() {
            val invalidModel = initValidModel().handbuchReferenceModel(null).build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTHANDBUCH_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validHandbuchWriteModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenHandbuchReferenceWahltagIDIsNull() {
            val invalidModel = initValidModel().handbuchReferenceModel(initValidHandbuchReferenceModel().wahltagID(null).build()).build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTHANDBUCH_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validHandbuchWriteModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenHandbuchReferenceWahltagIDIsEmptyString() {
            val invalidModel = initValidModel().handbuchReferenceModel(initValidHandbuchReferenceModel().wahltagID("").build()).build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTHANDBUCH_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validHandbuchWriteModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenHandbuchReferenceWahltagIDIsBlankString() {
            val invalidModel = initValidModel().handbuchReferenceModel(initValidHandbuchReferenceModel().wahltagID("      ").build()).build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTHANDBUCH_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validHandbuchWriteModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenHandbuchReferenceWahlbezirksArtIsNull() {
            val invalidModel = initValidModel().handbuchReferenceModel(initValidHandbuchReferenceModel().wahlbezirksart(null).build()).build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTHANDBUCH_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validHandbuchWriteModelOrThrow(invalidModel)).isSameAs(mockedWlsException);

        }

        private HandbuchWriteModel.HandbuchWriteModelBuilder initValidModel() {
            return HandbuchWriteModel.builder().handbuchReferenceModel(initValidHandbuchReferenceModel().build());
        }

        private HandbuchReferenceModel.HandbuchReferenceModelBuilder initValidHandbuchReferenceModel() {
            return HandbuchReferenceModel.builder().wahlbezirksart(WahlbezirkArtModel.BWB).wahltagID("wahltagID");
        }
    }
}
