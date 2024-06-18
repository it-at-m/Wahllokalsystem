package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand.Wahlvorstand;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand.WahlvorstandFunktion;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand.WahlvorstandRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand.Wahlvorstandsmitglied;
import de.muenchen.oss.wahllokalsystem.eaiservice.exception.NotFoundException;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandsaktualisierungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandsmitgliedAktualisierungDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WahlvorstandServiceTest {

    @Mock
    WahlvorstandRepository wahlvorstandRepository;

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    WahlvorstandMapper wahlvorstandMapper;

    @Mock
    WahlvorstandValidator wahlvorstandValidator;

    @InjectMocks
    WahlvorstandService unitUnderTest;

    @Nested
    class GetWahlvorstandForWahlbezirk {

        @Test
        void foundData() {
            val wahlbezirkID = UUID.randomUUID();

            val mockedEntity = new Wahlvorstand();
            val mockedMappedEntity = new WahlvorstandDTO("wahlbezirkID", Collections.emptySet());

            Mockito.when(wahlvorstandRepository.findFirstByWahlbezirkID(wahlbezirkID)).thenReturn(Optional.of(mockedEntity));
            Mockito.when(wahlvorstandMapper.toDTO(mockedEntity)).thenReturn(mockedMappedEntity);

            val result = unitUnderTest.getWahlvorstandForWahlbezirk(wahlbezirkID.toString());

            Assertions.assertThat(result).isSameAs(mockedMappedEntity);
        }

        @Test
        void foundNoData() {
            val wahlbezirkID = UUID.randomUUID();

            Mockito.when(wahlvorstandRepository.findFirstByWahlbezirkID(wahlbezirkID)).thenReturn(Optional.empty());

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlvorstandForWahlbezirk(wahlbezirkID.toString()))
                    .usingRecursiveComparison().isEqualTo(new NotFoundException(wahlbezirkID, Wahlvorstand.class));
        }

        @Test
        void exceptionForMalformedWahlbezirkID() {
            val wahlbezirkID = "noAUUID";

            val mockedException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.ID_NICHT_KONVERTIERBAR)).thenReturn(mockedException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlvorstandForWahlbezirk(wahlbezirkID)).isSameAs(mockedException);
        }

        @Test
        void exceptionWhenWahlbezirkIDIsNotValid() {
            val wahlbezirkID = UUID.randomUUID();

            val mockedValidationException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidationException).when(wahlvorstandValidator).validateWahlbezirkIDOrThrow(wahlbezirkID.toString());

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlvorstandForWahlbezirk(wahlbezirkID.toString()))
                    .isSameAs(mockedValidationException);
        }
    }

    @Nested
    class SetAnwesenheit {

        @Test
        void existingDataIsUpdated() {
            val wahlbezirkID = UUID.randomUUID();
            val mitglied1 = new WahlvorstandsmitgliedAktualisierungDTO(UUID.randomUUID().toString(), true);
            val mitglied2 = new WahlvorstandsmitgliedAktualisierungDTO(UUID.randomUUID().toString(), false);
            val mitgliederAktualisierung = Set.of(mitglied1, mitglied2);
            val aktualisierung = new WahlvorstandsaktualisierungDTO(wahlbezirkID.toString(), mitgliederAktualisierung, LocalDateTime.now());

            val mockedEntityLastSavedDateTime = LocalDateTime.now().minusDays(1);
            val mockedEntityMitglied1 = new Wahlvorstandsmitglied("", "", WahlvorstandFunktion.B, false, mockedEntityLastSavedDateTime);
            mockedEntityMitglied1.setId(UUID.fromString(mitglied1.identifikator()));
            // no mitglied2 is intended because request should match only a subset of existing data
            val mockedEntityMitglied3 = new Wahlvorstandsmitglied("", "", WahlvorstandFunktion.SWB, true, mockedEntityLastSavedDateTime);
            mockedEntityMitglied3.setId(UUID.randomUUID());
            val mockedEntity = new Wahlvorstand(wahlbezirkID, Set.of(mockedEntityMitglied1, mockedEntityMitglied3));

            Mockito.when(wahlvorstandRepository.findFirstByWahlbezirkID(wahlbezirkID)).thenReturn(Optional.of(mockedEntity));

            unitUnderTest.setAnwesenheit(aktualisierung);

            val saveArgumentCaptor = ArgumentCaptor.forClass(Wahlvorstand.class);
            Mockito.verify(wahlvorstandRepository).save(saveArgumentCaptor.capture());

            val capturedSavedArgument = saveArgumentCaptor.getValue();
            val expectedMitglied1 = new Wahlvorstandsmitglied("", "", WahlvorstandFunktion.B, true, aktualisierung.anwesenheitBeginn());
            expectedMitglied1.setId(UUID.fromString(mitglied1.identifikator()));
            val expectedMitglied3 = new Wahlvorstandsmitglied("", "", WahlvorstandFunktion.SWB, true, mockedEntityLastSavedDateTime);
            expectedMitglied3.setId(mockedEntityMitglied3.getId());
            val expectedSavedArgument = new Wahlvorstand(wahlbezirkID, Set.of(expectedMitglied1, expectedMitglied3));
            Assertions.assertThat(capturedSavedArgument).isEqualTo(expectedSavedArgument);
        }

        @Test
        void exceptionWhenWahlvorstandDoesNotExists() {
            val wahlbezirkID = UUID.randomUUID();
            val mitglied1 = new WahlvorstandsmitgliedAktualisierungDTO(UUID.randomUUID().toString(), true);
            val mitglied2 = new WahlvorstandsmitgliedAktualisierungDTO(UUID.randomUUID().toString(), false);
            val mitgliederAktualisierung = Set.of(mitglied1, mitglied2);
            val aktualisierung = new WahlvorstandsaktualisierungDTO(wahlbezirkID.toString(), mitgliederAktualisierung, LocalDateTime.now());

            Mockito.when(wahlvorstandRepository.findFirstByWahlbezirkID(wahlbezirkID)).thenReturn(Optional.empty());

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setAnwesenheit(aktualisierung)).usingRecursiveComparison()
                    .isEqualTo(new NotFoundException(wahlbezirkID, Wahlvorstand.class));

            Mockito.verify(wahlvorstandRepository, Mockito.times(0)).save(Mockito.any(Wahlvorstand.class));
        }

        @Test
        void validationFailed() {
            val aktualisierung = new WahlvorstandsaktualisierungDTO(UUID.randomUUID().toString(), Collections.emptySet(), LocalDateTime.now());

            val mockedValidationException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidationException).when(wahlvorstandValidator).valideSaveAnwesenheitDataOrThrow(aktualisierung);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setAnwesenheit(aktualisierung)).isSameAs(mockedValidationException);
        }

        @Test
        void exceptionBecauseOfWahlbezirkIDOfParameterIsInvalid() {
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.ID_NICHT_KONVERTIERBAR)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(
                    () -> unitUnderTest.setAnwesenheit(new WahlvorstandsaktualisierungDTO("malformedID", Collections.emptySet(), LocalDateTime.now())))
                    .isSameAs(mockedWlsException);
        }
    }

}
