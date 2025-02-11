package de.muenchen.oss.wahllokalsystem.adminservice.client.basisdaten;

import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.model.WahltagDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.WahltagModel;
import java.time.LocalDate;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahltagClientMapperTest {

    private final WahltagClientMapper unitUnderTest = Mappers.getMapper(WahltagClientMapper.class);

    @Nested
    class FromListOfWahltagDTOtoListOfWahltagModel {

        @Test
        void should_returnNull_when_nullIsGiven() {
            Assertions.assertThat(unitUnderTest.fromListOfWahltagDTOtoListOfWahltagModel(null)).isNull();
        }

        @Test
        void should_mapToListOfModel_when_givenListOfDTO() {
            val nowDate = LocalDate.now();
            val dtosToMap = List.of(
                    new WahltagDTO().wahltagID("wahltagID1").wahltag(nowDate).beschreibung("beschreibung").nummer("1"),
                    new WahltagDTO().wahltagID("wahltagID2").wahltag(nowDate).beschreibung("beschreibung").nummer("2"),
                    new WahltagDTO().wahltagID("wahltagID3").wahltag(nowDate).beschreibung("beschreibung").nummer("3"));

            dtosToMap.forEach(wahltag -> Assertions.assertThat(wahltag).hasNoNullFieldsOrProperties());

            val result = unitUnderTest.fromListOfWahltagDTOtoListOfWahltagModel(dtosToMap);

            val expectedWahltage = List.of(
                    new WahltagModel("wahltagID1", nowDate, "beschreibung", "1"),
                    new WahltagModel("wahltagID2", nowDate, "beschreibung", "2"),
                    new WahltagModel("wahltagID3", nowDate, "beschreibung", "3"));

            Assertions.assertThat(result).containsExactlyInAnyOrderElementsOf(expectedWahltage);
        }
    }
}
