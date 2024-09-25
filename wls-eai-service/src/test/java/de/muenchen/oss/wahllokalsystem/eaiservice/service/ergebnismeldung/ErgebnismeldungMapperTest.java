package de.muenchen.oss.wahllokalsystem.eaiservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.AWerte;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.BWerte;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.Ergebnis;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.Ergebnismeldung;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.Meldungsart;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.UngueltigeStimmzettel;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.WahlbriefeWerte;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahlart;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.dto.WahlartDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto.AWerteDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto.BWerteDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto.ErgebnisDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto.ErgebnismeldungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto.MeldungsartDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto.UngueltigeStimmzettelDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto.WahlbriefeWerteDTO;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class ErgebnismeldungMapperTest {

    private final ErgebnismeldungMapper unitUnderTest = Mappers.getMapper(ErgebnismeldungMapper.class);

    @Nested
    class ToEntity {

        @Test
        void isMapped() {
            val wahlID = "wahlID1";
            val wahlbezirkID = "00000000-0000-0000-0000-000000000001";
            val meldungsart = MeldungsartDTO.NIEDERSCHRIFT;
            val aWerte = new AWerteDTO(3L, 2L);
            val bWerte = new BWerteDTO(4L, 3L, 2L);
            val wahlbriefeWerte = new WahlbriefeWerteDTO(3L);
            val ungueltigeStimmzettelDTOList = Set.of(new UngueltigeStimmzettelDTO("test1", 4L, "wahlvorschlagID1"),
                    new UngueltigeStimmzettelDTO("test2", 5L, "wahlvorschlagID2"));
            val ungueltigeStimmzettelAnzahl = 4L;
            val ergebnisse = Set.of(new ErgebnisDTO("test1", 5L, 3L, "wahlvorschlagID1", "kandidatID1"),
                    new ErgebnisDTO("test2", 6L, 4L, "wahlvorschlagID2", "kandidatID2"));
            val wahlart = WahlartDTO.BTW;

            val entityToMap = new ErgebnismeldungDTO(wahlbezirkID, wahlID, meldungsart, aWerte, bWerte, wahlbriefeWerte, ungueltigeStimmzettelDTOList,
                    ungueltigeStimmzettelAnzahl, ergebnisse, wahlart);

            val result = unitUnderTest.toEntity(entityToMap);

            val expectedAWerte = new AWerte();
            expectedAWerte.setA1(3L);
            expectedAWerte.setA2(2L);

            val expectedBWerte = new BWerte();
            expectedBWerte.setB(4L);
            expectedBWerte.setB1(3L);
            expectedBWerte.setB2(2L);

            val expectedWahlbriefeWerte = new WahlbriefeWerte();
            expectedWahlbriefeWerte.setZurueckgewiesenGesamt(3L);

            val expectedUngueltigeStimmzettel1 = new UngueltigeStimmzettel();
            expectedUngueltigeStimmzettel1.setAnzahl(4L);
            expectedUngueltigeStimmzettel1.setStimmenart("test1");
            expectedUngueltigeStimmzettel1.setWahlvorschlagID("wahlvorschlagID1");

            val expectedUngueltigeStimmzettel2 = new UngueltigeStimmzettel();
            expectedUngueltigeStimmzettel2.setAnzahl(5L);
            expectedUngueltigeStimmzettel2.setStimmenart("test2");
            expectedUngueltigeStimmzettel2.setWahlvorschlagID("wahlvorschlagID2");

            val expectedUngueltigeStimmzettelList = Set.of(expectedUngueltigeStimmzettel2,
                    expectedUngueltigeStimmzettel1);
            val expectedErgebnis1 = new Ergebnis();
            val expectedErgebnis2 = new Ergebnis();

            expectedErgebnis1.setStimmenart("test1");
            expectedErgebnis1.setWahlvorschlagsordnungszahl(5L);
            expectedErgebnis1.setErgebnis(3L);
            expectedErgebnis1.setKandidatID("kandidatID1");
            expectedErgebnis1.setWahlvorschlagID("wahlvorschlagID1");

            expectedErgebnis2.setStimmenart("test2");
            expectedErgebnis2.setWahlvorschlagsordnungszahl(6L);
            expectedErgebnis2.setErgebnis(4L);
            expectedErgebnis2.setKandidatID("kandidatID2");
            expectedErgebnis2.setWahlvorschlagID("wahlvorschlagID2");

            val expectedErgebnisse = Set.of(expectedErgebnis2, expectedErgebnis1);

            val expectedResult = new Ergebnismeldung(wahlbezirkID, wahlID, Meldungsart.NIEDERSCHRIFT, expectedAWerte,
                    expectedBWerte, expectedWahlbriefeWerte, expectedUngueltigeStimmzettelList, ungueltigeStimmzettelAnzahl, expectedErgebnisse, Wahlart.BTW);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

    }
}
