package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlbezirkDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkModel;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahlbezirkeClientMapperTest {

    private final WahlbezirkeClientMapper unitUnderTest = Mappers.getMapper(WahlbezirkeClientMapper.class);

    @Nested
    class FromClientDTOToModel {

        @Test
        void isMapped() {
            val dtoToMap = new WahlbezirkDTO();
            dtoToMap.setIdentifikator("identifikatorWahlbezirk1");
            dtoToMap.setWahlID("wahlID1");
            dtoToMap.setWahlbezirkArt(WahlbezirkDTO.WahlbezirkArtEnum.UWB);
            dtoToMap.setWahlnummer("wahlnummer1");
            dtoToMap.setNummer("nummerWahlbezirk1");
            dtoToMap.setWahltag(LocalDate.now().minusMonths(2));

            Assertions.assertThat(dtoToMap).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.fromClientDTOToModel(dtoToMap);

            val expectedWahlbezirk = new WahlbezirkModel(
                    "identifikatorWahlbezirk1",
                    WahlbezirkArtModel.UWB,
                    "nummerWahlbezirk1",
                    LocalDate.now().minusMonths(2),
                    "wahlnummer1",
                    "wahlID1");

            Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedWahlbezirk);
        }
    }

    @Nested
    class FromRemoteSetOfDTOsToSetOfModels {

        @Test
        void isMapped() {
            val wahlbezirk1 = new WahlbezirkDTO();
            wahlbezirk1.setIdentifikator("identifikatorWahlbezirk1");
            wahlbezirk1.setWahlID("wahlID1");
            wahlbezirk1.setWahlbezirkArt(WahlbezirkDTO.WahlbezirkArtEnum.UWB);
            wahlbezirk1.setWahlnummer("wahlnummer1");
            wahlbezirk1.setNummer("nummerWahlbezirk1");
            wahlbezirk1.setWahltag(LocalDate.now().minusMonths(2));

            val wahlbezirk2 = new WahlbezirkDTO();
            wahlbezirk2.setIdentifikator("identifikatorWahlbezirk2");
            wahlbezirk2.setWahlID("wahlID2");
            wahlbezirk2.setWahlbezirkArt(WahlbezirkDTO.WahlbezirkArtEnum.BWB);
            wahlbezirk2.setWahlnummer("wahlnummer2");
            wahlbezirk2.setNummer("nummerWahlbezirk2");
            wahlbezirk2.setWahltag(LocalDate.now());

            val wahlbezirk3 = new WahlbezirkDTO();
            wahlbezirk3.setIdentifikator("identifikatorWahlbezirk3");
            wahlbezirk3.setWahlID("wahlID1");
            wahlbezirk3.setWahlbezirkArt(WahlbezirkDTO.WahlbezirkArtEnum.UWB);
            wahlbezirk3.setWahlnummer("wahlnummer1");
            wahlbezirk3.setNummer("nummerWahlbezirk3");
            wahlbezirk3.setWahltag(LocalDate.now().minusMonths(2));

            val dtosToMap = Set.of(wahlbezirk1, wahlbezirk2, wahlbezirk3);

            Assertions.assertThat(dtosToMap).size().isEqualTo(3);
            dtosToMap.forEach(wahlbezirk -> Assertions.assertThat(wahlbezirk).hasNoNullFieldsOrProperties());

            val result = unitUnderTest.fromRemoteSetOfDTOsToSetOfModels(dtosToMap);

            val expectedWahlbezirke = List.of(
                    new WahlbezirkModel(
                            "identifikatorWahlbezirk1",
                            WahlbezirkArtModel.UWB,
                            "nummerWahlbezirk1",
                            LocalDate.now().minusMonths(2),
                            "wahlnummer1",
                            "wahlID1"),
                    new WahlbezirkModel(
                            "identifikatorWahlbezirk2",
                            WahlbezirkArtModel.BWB,
                            "nummerWahlbezirk2",
                            LocalDate.now(),
                            "wahlnummer2",
                            "wahlID2"),
                    new WahlbezirkModel(
                            "identifikatorWahlbezirk3",
                            WahlbezirkArtModel.UWB,
                            "nummerWahlbezirk3",
                            LocalDate.now().minusMonths(2),
                            "wahlnummer1",
                            "wahlID1"));

            Assertions.assertThat(result).containsExactlyInAnyOrderElementsOf(expectedWahlbezirke);
        }
    }
}
