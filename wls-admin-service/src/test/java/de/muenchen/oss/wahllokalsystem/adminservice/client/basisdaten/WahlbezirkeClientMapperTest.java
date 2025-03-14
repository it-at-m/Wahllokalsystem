package de.muenchen.oss.wahllokalsystem.adminservice.client.basisdaten;

import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.model.WahlbezirkDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahlbezirkModel;
import java.time.LocalDate;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahlbezirkeClientMapperTest {

    private final WahlbezirkeClientMapper unitUnderTest = Mappers.getMapper(WahlbezirkeClientMapper.class);

    @Nested
    class ToModelList {

        @Test
        void should_returnNull_when_nullIsGiven() {
            Assertions.assertThat(unitUnderTest.toModelList(null)).isNull();
        }

        @Test
        void should_mapToListOfModel_when_givenListOfDTO() {
            val wahltag = LocalDate.now();

            val dtosToMap = List.of(
                    new WahlbezirkDTO().wahlbezirkID("wahlbezirkID1").wahlbezirkart(WahlbezirkDTO.WahlbezirkartEnum.UWB).nummer("nummer").wahltag(wahltag)
                            .wahlnummer("wahlnummer").wahlID("wahlID1"),
                    new WahlbezirkDTO().wahlbezirkID("wahlbezirkID2").wahlbezirkart(WahlbezirkDTO.WahlbezirkartEnum.UWB).nummer("nummer").wahltag(wahltag)
                            .wahlnummer("wahlnummer").wahlID("wahlID1"),
                    new WahlbezirkDTO().wahlbezirkID("wahlbezirkID3").wahlbezirkart(WahlbezirkDTO.WahlbezirkartEnum.UWB).nummer("nummer").wahltag(wahltag)
                            .wahlnummer("wahlnummer").wahlID("wahlID1"));

            dtosToMap.forEach(wahlbezirk -> Assertions.assertThat(wahlbezirk).hasNoNullFieldsOrProperties());

            val result = unitUnderTest.toModelList(dtosToMap);

            val expectedWahlbezirke = List.of(
                    new WahlbezirkModel("wahlbezirkID1", WahlbezirkArtModel.UWB, "nummer", wahltag, "wahlnummer", "wahlID1"),
                    new WahlbezirkModel("wahlbezirkID2", WahlbezirkArtModel.UWB, "nummer", wahltag, "wahlnummer", "wahlID1"),
                    new WahlbezirkModel("wahlbezirkID3", WahlbezirkArtModel.UWB, "nummer", wahltag, "wahlnummer", "wahlID1"));

            Assertions.assertThat(result).containsExactlyInAnyOrderElementsOf(expectedWahlbezirke);
        }
    }
}
