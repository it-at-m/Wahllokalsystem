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

            val entityToMap = new ErgebnismeldungDTO(wahlbezirkID, wahlID, meldungsart, aWerte, bWerte, wahlbriefeWerte, ungueltigeStimmzettelDTOList, ungueltigeStimmzettelAnzahl, ergebnisse, wahlart);

            val result = unitUnderTest.toEntity(entityToMap);

            val aWerte2 = new AWerte();
            aWerte2.setA1(3L);
            aWerte2.setA2(2L);

            val bWerte2 = new BWerte();
            bWerte2.setB(4L);
            bWerte2.setB1(3L);
            bWerte2.setB2(2L);

            val wahlbriefeWerte2 = new WahlbriefeWerte();
            wahlbriefeWerte2.setZurueckgewiesenGesamt(3L);

            val ungueltigeStimmzettel2 = new UngueltigeStimmzettel();
            ungueltigeStimmzettel2.setAnzahl(4L);
            ungueltigeStimmzettel2.setStimmenart("test1");
            ungueltigeStimmzettel2.setWahlvorschlagID("wahlvorschlagID1");

            val ungueltigeStimmzettel3 = new UngueltigeStimmzettel();
            ungueltigeStimmzettel3.setAnzahl(5L);
            ungueltigeStimmzettel3.setStimmenart("test2");
            ungueltigeStimmzettel3.setWahlvorschlagID("wahlvorschlagID2");

            val ungueltigeStimmzettelList = Set.of(ungueltigeStimmzettel3,
                    ungueltigeStimmzettel2);
            val ergebnis1 = new Ergebnis();
            val ergebnis2 = new Ergebnis();

            ergebnis1.setStimmenart("test1");
            ergebnis1.setWahlvorschlagsordnungszahl(5L);
            ergebnis1.setErgebnis(3L);
            ergebnis1.setKandidatID("kandidatID1");
            ergebnis1.setWahlvorschlagID("wahlvorschlagID1");

            ergebnis2.setStimmenart("test2");
            ergebnis2.setWahlvorschlagsordnungszahl(6L);
            ergebnis2.setErgebnis(4L);
            ergebnis2.setKandidatID("kandidatID2");
            ergebnis2.setWahlvorschlagID("wahlvorschlagID2");

            val ergebnisse2 = Set.of(ergebnis2, ergebnis1);

            val expectedResult = new Ergebnismeldung(wahlbezirkID, wahlID, Meldungsart.NIEDERSCHRIFT, aWerte2,
                    bWerte2, wahlbriefeWerte2, ungueltigeStimmzettelList, ungueltigeStimmzettelAnzahl, ergebnisse2, Wahlart.BTW);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

    }
}
