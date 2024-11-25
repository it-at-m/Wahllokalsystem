package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.awerte;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.WahlberechtigteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class AWerteClientMapperTest {

    private final AWerteClientMapper unitUnderTest = Mappers.getMapper(AWerteClientMapper.class);

    @Nested
    class fromRemoteClientWahlberechtigteDtoToAWerteModel {

        @Test
        void should_returnMappedAWerteModel_whenWahlberechtigteDto_isGiven() {
            val dtoToMap = new WahlberechtigteDTO();
            dtoToMap.setWahlbezirkID("wahlbezirkID");
            dtoToMap.setWahlID("wahlID");
            dtoToMap.setA1(2L);
            dtoToMap.setA2(3L);
            dtoToMap.setA3(5L);

            Assertions.assertThat(dtoToMap).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.fromRemoteClientWahlberechtigteDtoToAWerteModel(dtoToMap);

            val expectedAWerteModel = new AWerteModel(new BezirkUndWahlID("wahlID", "wahlbezirkID"), 2, 3L);
            Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedAWerteModel);
        }
    }

    @Nested
    class fromRemoteClientListOfWahlberechtigteDtoToListOfAWerteModel {

        @Test
        void should_returnMappedListOfAWerteModel_whenListOfWahlberechtigteDto_isGiven() {
            val wbDtoList = createListOfWahlberechtigteDTO();
            Assertions.assertThat(wbDtoList).size().isEqualTo(3);
            wbDtoList.forEach(wahlberechtigte -> Assertions.assertThat(wahlberechtigte).hasNoNullFieldsOrProperties());

            val result = unitUnderTest.fromRemoteClientListOfWahlberechtigteDtoToListOfAWerteModel(wbDtoList);

            val awm1 = new AWerteModel(new BezirkUndWahlID("wahlID", "wahlbezirkID1"), 2, 3L);
            val awm2 = new AWerteModel(new BezirkUndWahlID("wahlID", "wahlbezirkID2"), 3, 4L);
            val awm3 = new AWerteModel(new BezirkUndWahlID("wahlID", "wahlbezirkID3"), 4, 5L);
            val expectedAWerteList = List.of(awm1, awm2, awm3);

            Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedAWerteList);
            Assertions.assertThat(result).containsExactlyInAnyOrderElementsOf(expectedAWerteList);
        }
    }

    private List<WahlberechtigteDTO> createListOfWahlberechtigteDTO() {
        val wb1 = new WahlberechtigteDTO();
        wb1.setWahlbezirkID("wahlbezirkID1");
        wb1.setWahlID("wahlID");
        wb1.setA1(2L);
        wb1.setA2(3L);
        wb1.setA3(5L);
        val wb2 = new WahlberechtigteDTO();
        wb2.setWahlbezirkID("wahlbezirkID2");
        wb2.setWahlID("wahlID");
        wb2.setA1(3L);
        wb2.setA2(4L);
        wb2.setA3(7L);
        val wb3 = new WahlberechtigteDTO();
        wb3.setWahlbezirkID("wahlbezirkID3");
        wb3.setWahlID("wahlID");
        wb3.setA1(4L);
        wb3.setA2(5L);
        wb3.setA3(9L);
        return List.of(wb1, wb2, wb3);
    }
}
