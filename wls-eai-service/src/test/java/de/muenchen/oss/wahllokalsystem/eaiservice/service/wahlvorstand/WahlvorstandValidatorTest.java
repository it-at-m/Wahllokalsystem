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
        void should_notThrowException_when_wahlbezirkIDIsValid() {
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validateWahlbezirkIDOrThrow(UUID.randomUUID().toString()));
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDIsNull() {
            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLVORSTAND_SUCHKRITERIEN_UNVOLLSTAENDIG))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateWahlbezirkIDOrThrow(null)).isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDIsEmpty() {
            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLVORSTAND_SUCHKRITERIEN_UNVOLLSTAENDIG))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateWahlbezirkIDOrThrow("")).isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDIsBlank() {
            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLVORSTAND_SUCHKRITERIEN_UNVOLLSTAENDIG))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateWahlbezirkIDOrThrow("   ")).isSameAs(mockedFachlicheWlsException);
        }
    }

    @Nested
    class ValidateSaveAnwesenheitDataOrThrow {

        @Test
        void should_notThrowException_when_aktualisierungIsValid() {
            val validDTO = initValidAktualisierung().build();

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validateSaveAnwesenheitDataOrThrow(validDTO));
        }

        @Test
        void should_throwWlsException_when_aktualisierungIDIsNull() {
            val mockedValidationException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(
                    de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.exception.ExceptionConstants.DATENALLGEMEIN_PARAMETER_FEHLEN))
                    .thenReturn(mockedValidationException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateSaveAnwesenheitDataOrThrow(null)).isSameAs(mockedValidationException);
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDIsNull() {
            val invalidDTO = initValidAktualisierung().wahlbezirkID(null).build();

            val mockedValidationException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEANWESENHEIT_BEZIRKID_FEHLT))
                    .thenReturn(mockedValidationException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateSaveAnwesenheitDataOrThrow(invalidDTO)).isSameAs(mockedValidationException);
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDIsEmpty() {
            val invalidDTO = initValidAktualisierung().wahlbezirkID("").build();

            val mockedValidationException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEANWESENHEIT_BEZIRKID_FEHLT))
                    .thenReturn(mockedValidationException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateSaveAnwesenheitDataOrThrow(invalidDTO)).isSameAs(mockedValidationException);
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDIsBlank() {
            val invalidDTO = initValidAktualisierung().wahlbezirkID("   ").build();

            val mockedValidationException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEANWESENHEIT_BEZIRKID_FEHLT))
                    .thenReturn(mockedValidationException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateSaveAnwesenheitDataOrThrow(invalidDTO)).isSameAs(mockedValidationException);
        }

        @Test
        void should_throwWlsException_when_anwesenheitbeginnIsNull() {
            val invalidDTO = initValidAktualisierung().anwesenheitBeginn(null).build();

            val mockedValidationException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEANWESENHEIT_ANWESENHEITBEGINN_FEHLT))
                    .thenReturn(mockedValidationException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateSaveAnwesenheitDataOrThrow(invalidDTO)).isSameAs(mockedValidationException);
        }

        @Nested
        class ExceptionWhenMitgliederIdIsInvalid {

            @Test
            void should_throwWlsException_when_mitgliederIDIsNull() {
                val invalidDTO = initValidAktualisierung().mitglieder(
                        Set.of(initValidMitgliedAktualisierung().build(), initValidMitgliedAktualisierung().identifikator(null).build())).build();

                val mockedValidationException = FachlicheWlsException.withCode("").buildWithMessage("");
                Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEANWESENHEIT_IDENTIFIKATOR_FEHLT))
                        .thenReturn(mockedValidationException);

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateSaveAnwesenheitDataOrThrow(invalidDTO))
                        .isSameAs(mockedValidationException);
            }

            @Test
            void should_throwWlsException_when_mitgliederIDIsEmpty() {
                val invalidDTO = initValidAktualisierung().mitglieder(
                        Set.of(initValidMitgliedAktualisierung().build(), initValidMitgliedAktualisierung().identifikator("").build())).build();

                val mockedValidationException = FachlicheWlsException.withCode("").buildWithMessage("");
                Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEANWESENHEIT_IDENTIFIKATOR_FEHLT))
                        .thenReturn(mockedValidationException);

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateSaveAnwesenheitDataOrThrow(invalidDTO))
                        .isSameAs(mockedValidationException);
            }

            @Test
            void should_throwWlsException_when_mitgliederIDIsBlank() {
                val invalidDTO = initValidAktualisierung().mitglieder(
                        Set.of(initValidMitgliedAktualisierung().build(), initValidMitgliedAktualisierung().identifikator("  ").build())).build();

                val mockedValidationException = FachlicheWlsException.withCode("").buildWithMessage("");
                Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEANWESENHEIT_IDENTIFIKATOR_FEHLT))
                        .thenReturn(mockedValidationException);

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateSaveAnwesenheitDataOrThrow(invalidDTO))
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
