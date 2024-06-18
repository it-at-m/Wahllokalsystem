package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandsaktualisierungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandsmitgliedAktualisierungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
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
class WahlvorstandValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    WahlvorstandValidator unitUnderTest;

    @Nested
    class ValidateWahlbezirkIDOrThrow {

        @Test
        void noExceptionWhenIdIsValid() {
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validateWahlbezirkIDOrThrow(UUID.randomUUID().toString()));
        }

        @Test
        void exceptionWhenIDIsNUll() {
            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLVORSTAND_SUCHKRITERIEN_UNVOLLSTAENDIG))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateWahlbezirkIDOrThrow(null)).isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void exceptionWhenIDIsEmptyString() {
            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLVORSTAND_SUCHKRITERIEN_UNVOLLSTAENDIG))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateWahlbezirkIDOrThrow("")).isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void exceptionWhenIDIsBlankString() {
            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLVORSTAND_SUCHKRITERIEN_UNVOLLSTAENDIG))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateWahlbezirkIDOrThrow("   ")).isSameAs(mockedFachlicheWlsException);
        }
    }

    @Nested
    class ValideSaveAnwesenheitDataOrThrow {

        @Test
        void noExceptionWhenAktualisierungIsValid() {
            val validDTO = initValidAktualisierung().build();

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.valideSaveAnwesenheitDataOrThrow(validDTO));
        }

        @Test
        void exceptionWhenAktualisierungIsNull() {
            val mockedValidationException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(
                    de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.exception.ExceptionConstants.DATENALLGEMEIN_PARAMETER_FEHLEN))
                    .thenReturn(mockedValidationException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideSaveAnwesenheitDataOrThrow(null)).isSameAs(mockedValidationException);
        }

        @Test
        void exceptionWhenWahlbezirkIdIsNull() {
            val invalidDTO = initValidAktualisierung().wahlbezirkID(null).build();

            val mockedValidationException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEANWESENHEIT_BEZIRKID_FEHLT))
                    .thenReturn(mockedValidationException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideSaveAnwesenheitDataOrThrow(invalidDTO)).isSameAs(mockedValidationException);
        }

        @Test
        void exceptionWhenWahlbezirkIdIsEmpty() {
            val invalidDTO = initValidAktualisierung().wahlbezirkID("").build();

            val mockedValidationException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEANWESENHEIT_BEZIRKID_FEHLT))
                    .thenReturn(mockedValidationException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideSaveAnwesenheitDataOrThrow(invalidDTO)).isSameAs(mockedValidationException);
        }

        @Test
        void exceptionWhenWahlbezirkIdIsBlank() {
            val invalidDTO = initValidAktualisierung().wahlbezirkID("   ").build();

            val mockedValidationException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEANWESENHEIT_BEZIRKID_FEHLT))
                    .thenReturn(mockedValidationException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideSaveAnwesenheitDataOrThrow(invalidDTO)).isSameAs(mockedValidationException);
        }

        @Test
        void exceptionWhenAnwesenheitBeginnIsNull() {
            val invalidDTO = initValidAktualisierung().anwesenheitBeginn(null).build();

            val mockedValidationException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEANWESENHEIT_ANWESENHEITBEGINN_FEHLT))
                    .thenReturn(mockedValidationException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideSaveAnwesenheitDataOrThrow(invalidDTO)).isSameAs(mockedValidationException);

        }

        @Nested
        class ExceptionWhenMitgliederIdIsInvalid {

            @Test
            void isNull() {
                val invalidDTO = initValidAktualisierung().mitglieder(
                        Set.of(initValidMitgliedAktualisierung().build(), initValidMitgliedAktualisierung().identifikator(null).build())).build();

                val mockedValidationException = FachlicheWlsException.withCode("").buildWithMessage("");
                Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEANWESENHEIT_IDENTIFIKATOR_FEHLT))
                        .thenReturn(mockedValidationException);

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideSaveAnwesenheitDataOrThrow(invalidDTO))
                        .isSameAs(mockedValidationException);
            }

            @Test
            void isEmptyString() {
                val invalidDTO = initValidAktualisierung().mitglieder(
                        Set.of(initValidMitgliedAktualisierung().build(), initValidMitgliedAktualisierung().identifikator("").build())).build();

                val mockedValidationException = FachlicheWlsException.withCode("").buildWithMessage("");
                Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEANWESENHEIT_IDENTIFIKATOR_FEHLT))
                        .thenReturn(mockedValidationException);

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideSaveAnwesenheitDataOrThrow(invalidDTO))
                        .isSameAs(mockedValidationException);
            }

            @Test
            void isBankString() {
                val invalidDTO = initValidAktualisierung().mitglieder(
                        Set.of(initValidMitgliedAktualisierung().build(), initValidMitgliedAktualisierung().identifikator("  ").build())).build();

                val mockedValidationException = FachlicheWlsException.withCode("").buildWithMessage("");
                Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEANWESENHEIT_IDENTIFIKATOR_FEHLT))
                        .thenReturn(mockedValidationException);

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.valideSaveAnwesenheitDataOrThrow(invalidDTO))
                        .isSameAs(mockedValidationException);
            }
        }

        private WahlvorstandsaktualisierungDTO.WahlvorstandsaktualisierungDTOBuilder initValidAktualisierung() {
            return WahlvorstandsaktualisierungDTO.builder().anwesenheitBeginn(LocalDateTime.now()).wahlbezirkID("wahlbezirkID")
                    .mitglieder(Collections.emptySet());
        }

        private WahlvorstandsmitgliedAktualisierungDTO.WahlvorstandsmitgliedAktualisierungDTOBuilder initValidMitgliedAktualisierung() {
            return WahlvorstandsmitgliedAktualisierungDTO.builder().identifikator("mitgliedId");
        }
    }

}
