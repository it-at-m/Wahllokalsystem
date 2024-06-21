package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand.Wahlvorstand;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand.Wahlvorstandsmitglied;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand.WahlvorstandsmitgliedsFunktion;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandsmitgliedDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandsmitgliedsFunktionDTO;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahlvorstandMapperTest {

    private final WahlvorstandMapper unitUnderTest = Mappers.getMapper(WahlvorstandMapper.class);

    @Test
    void toDTO() {
        val wahlbezirkID = UUID.randomUUID();
        val mitglied1 = new Wahlvorstandsmitglied("vorname1", "nachname1", WahlvorstandsmitgliedsFunktion.SWB, true, LocalDateTime.now());
        mitglied1.setId(UUID.randomUUID());
        val mitglied2 = new Wahlvorstandsmitglied("vorname2", "nachname2", WahlvorstandsmitgliedsFunktion.B, false, LocalDateTime.now().minusDays(1));
        mitglied2.setId(UUID.randomUUID());
        val mitglieder = Set.of(mitglied1, mitglied2);
        val entityToMap = new Wahlvorstand(wahlbezirkID, mitglieder);

        val result = unitUnderTest.toDTO(entityToMap);

        val expectedMitglieder = Set.of(
                new WahlvorstandsmitgliedDTO(mitglied1.getId().toString(), mitglied1.getVorname(), mitglied1.getNachname(),
                        WahlvorstandsmitgliedsFunktionDTO.SWB, true),
                new WahlvorstandsmitgliedDTO(mitglied2.getId().toString(), mitglied2.getVorname(), mitglied2.getNachname(), WahlvorstandsmitgliedsFunktionDTO.B,
                        false));
        val expectedResult = new WahlvorstandDTO(wahlbezirkID.toString(), expectedMitglieder);

        Assertions.assertThat(result).isEqualTo(expectedResult);
    }

}
