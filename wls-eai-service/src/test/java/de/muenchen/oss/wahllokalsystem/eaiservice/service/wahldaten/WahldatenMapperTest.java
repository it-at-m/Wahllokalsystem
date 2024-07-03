package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahldaten;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Stimmzettelgebiet;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Stimmzettelgebietsart;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahl;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahlart;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahlbezirk;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahltag;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.dto.WahlartDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.BasisstrukturdatenDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.StimmzettelgebietDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.StimmzettelgebietsartDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlberechtigteDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlbezirkArtDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlbezirkDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahltagDTO;
import java.time.LocalDate;
import java.util.UUID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahldatenMapperTest {

    private final WahldatenMapper unitUnderTest = Mappers.getMapper(WahldatenMapper.class);

    @Nested
    class ToDTO {

        @Test
        void ofWahltag() {
            val tag = LocalDate.now();
            val beschreibung = "beschreibung";
            val nummer = "nummer";
            val id = UUID.randomUUID();

            val entityToMap = new Wahltag(tag, beschreibung, nummer);
            entityToMap.setId(id);

            val result = unitUnderTest.toDTO(entityToMap);

            val expectedResult = new WahltagDTO(id.toString(), tag, beschreibung, nummer);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void ofWahl() {
            val wahlID = UUID.randomUUID();
            val wahltag = LocalDate.now();
            val name = "name";
            val nummer = "nummer";

            val entityToMap = new Wahl(name, Wahlart.BTW, new Wahltag(wahltag, null, null), nummer);
            entityToMap.setId(wahlID);

            val result = unitUnderTest.toDTO(entityToMap);

            val expectedResult = new WahlDTO(wahlID.toString(), name, WahlartDTO.BTW, wahltag, nummer);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void ofWahlbezirk() {
            val wahlbezirkID = UUID.randomUUID();
            val wahlID = UUID.randomUUID();
            val wahltag = LocalDate.now();
            val wahlbezirkNummer = "wahlbezirkNummer";
            val wahltagnummer = "wahltagnummer";

            val wahlForWahlbezirk = new Wahl(null, null, new Wahltag(wahltag, null, wahltagnummer), null);
            wahlForWahlbezirk.setId(wahlID);
            val entityToMap = new Wahlbezirk(WahlbezirkArtDTO.UWB, wahlbezirkNummer, new Stimmzettelgebiet(null, null, null, wahlForWahlbezirk), 0, 0, 0);
            entityToMap.setId(wahlbezirkID);

            val result = unitUnderTest.toDTO(entityToMap);

            val expectedResult = new WahlbezirkDTO(wahlbezirkID.toString(), WahlbezirkArtDTO.UWB, wahlbezirkNummer, wahltag, wahltagnummer, wahlID.toString());
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void ofStimmzettelgebiet() {
            val szgID = UUID.randomUUID();
            val szgNummer = "szgNummer";
            val szgName = "szgName";
            val wahltag = LocalDate.now();

            val entityToMap = new Stimmzettelgebiet(szgNummer, szgName, Stimmzettelgebietsart.SK, new Wahl(null, null, new Wahltag(wahltag, null, null), null));
            entityToMap.setId(szgID);

            val result = unitUnderTest.toDTO(entityToMap);

            val expectedResult = new StimmzettelgebietDTO(szgID.toString(), szgNummer, szgName, wahltag, StimmzettelgebietsartDTO.SK);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToWahlberechtigteDTO {

        @Test
        void isMapped() {
            val wahlID = UUID.randomUUID();
            val wahlbezirkID = UUID.randomUUID();
            val a1 = 10;
            val a2 = 20;
            val a3 = 30;

            val wahlOfWahlbezirk = new Wahl();
            wahlOfWahlbezirk.setId(wahlID);
            val entityToMap = new Wahlbezirk(null, null, new Stimmzettelgebiet(null, null, null, wahlOfWahlbezirk), a1, a2, a3);
            entityToMap.setId(wahlbezirkID);

            val result = unitUnderTest.toWahlberechtigteDTO(entityToMap);

            val expectedResult = new WahlberechtigteDTO(wahlID.toString(), wahlbezirkID.toString(), a1, a2, a3);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToBasisstrukturdatenDTO {

        @Test
        void isMapped() {
            val wahlID = UUID.randomUUID();
            val sgzID = UUID.randomUUID();
            val wbzID = UUID.randomUUID();
            val wahltag = LocalDate.now();

            val wahl = new Wahl();
            wahl.setId(wahlID);
            wahl.setWahltag(new Wahltag(wahltag, null, null));
            val stimmzettelgebiet = new Stimmzettelgebiet();
            stimmzettelgebiet.setId(sgzID);
            stimmzettelgebiet.setWahl(wahl);
            val entityToMap = new Wahlbezirk();
            entityToMap.setStimmzettelgebiet(stimmzettelgebiet);
            entityToMap.setId(wbzID);

            val result = unitUnderTest.toBasisstrukturdatenDTO(entityToMap);

            val expectedResult = new BasisstrukturdatenDTO(wahlID.toString(), sgzID.toString(), wbzID.toString(), wahltag);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

    }

}
