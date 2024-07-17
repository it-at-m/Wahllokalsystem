package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.ungueltigewahlscheine;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahlbezirkArtModel;
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
class UngueltigeWahlscheineValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    UngueltigeWahlscheineValidator unitUnderTest;

    @Nested
    class ValidUngueltigeWahlscheineReferenceOrThrow {

        final FachlicheWlsException mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

        @Test
        void noExceptionWhenModelIsValid() {
            val validModel = initValidModel().build();

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validUngueltigeWahlscheineReferenceOrThrow(validModel));
        }

        @Test
        void exceptionWhenModelIsNull() {
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETUNGUELTIGEWAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validUngueltigeWahlscheineReferenceOrThrow(null)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenWahtlagIDIsNull() {
            val invalidModel = initValidModel().wahltagID(null).build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETUNGUELTIGEWAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validUngueltigeWahlscheineReferenceOrThrow(invalidModel)).isSameAs(mockedWlsException);

        }

        @Test
        void exceptionWhenWahtlagIDIsEmpty() {
            val invalidModel = initValidModel().wahltagID("").build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETUNGUELTIGEWAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validUngueltigeWahlscheineReferenceOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenWahltagIDIsBlankString() {
            val invalidModel = initValidModel().wahltagID("   ").build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETUNGUELTIGEWAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validUngueltigeWahlscheineReferenceOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWheWahlbezirkartIsNull() {
            val invalidModel = initValidModel().wahlbezirksart(null).build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETUNGUELTIGEWAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validUngueltigeWahlscheineReferenceOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        private UngueltigeWahlscheineReferenceModel.UngueltigeWahlscheineReferenceModelBuilder initValidModel() {
            return UngueltigeWahlscheineReferenceModel.builder().wahltagID("wahltagID").wahlbezirksart(WahlbezirkArtModel.UWB);
        }

    }

    @Nested
    class ValidUngueltigeWahlscheineWriteModelOrThrow {

        final FachlicheWlsException mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

        @Test
        void noExceptionWhenModelIsValid() {
            val validModel = initValidModel().build();

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validUngueltigeWahlscheineWriteModelOrThrow(validModel));
        }

        @Test
        void exceptionWhenModelIsNull() {
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTUNGUELTIGEWS_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validUngueltigeWahlscheineWriteModelOrThrow(null)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenWahtlagIDIsNull() {
            val invalidModel = initValidModel().ungueltigeWahlscheineReferenceModel(initValidReferenceModel().wahltagID(null).build()).build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTUNGUELTIGEWS_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validUngueltigeWahlscheineWriteModelOrThrow(invalidModel)).isSameAs(mockedWlsException);

        }

        @Test
        void exceptionWhenWahtlagIDIsEmpty() {
            val invalidModel = initValidModel().ungueltigeWahlscheineReferenceModel(initValidReferenceModel().wahltagID("").build()).build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTUNGUELTIGEWS_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validUngueltigeWahlscheineWriteModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenWahltagIDIsBlankString() {
            val invalidModel = initValidModel().ungueltigeWahlscheineReferenceModel(initValidReferenceModel().wahltagID("   ").build()).build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTUNGUELTIGEWS_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validUngueltigeWahlscheineWriteModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWheWahlbezirkartIsNull() {
            val invalidModel = initValidModel().ungueltigeWahlscheineReferenceModel(initValidReferenceModel().wahlbezirksart(null).build()).build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTUNGUELTIGEWS_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validUngueltigeWahlscheineWriteModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenUngueltigeWahlscheineDataIsNull() {
            val invalidModel = initValidModel().ungueltigeWahlscheineData(null).build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTUNGUELTIGEWS_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validUngueltigeWahlscheineWriteModelOrThrow(invalidModel)).isSameAs(mockedWlsException);
        }

        private UngueltigeWahlscheineReferenceModel.UngueltigeWahlscheineReferenceModelBuilder initValidReferenceModel() {
            return UngueltigeWahlscheineReferenceModel.builder().wahltagID("wahltagID").wahlbezirksart(WahlbezirkArtModel.UWB);
        }

        private UngueltigeWahlscheineWriteModel.UngueltigeWahlscheineWriteModelBuilder initValidModel() {
            return UngueltigeWahlscheineWriteModel.builder().ungueltigeWahlscheineReferenceModel(initValidReferenceModel().build())
                    .ungueltigeWahlscheineData("data".getBytes());
        }

    }
}