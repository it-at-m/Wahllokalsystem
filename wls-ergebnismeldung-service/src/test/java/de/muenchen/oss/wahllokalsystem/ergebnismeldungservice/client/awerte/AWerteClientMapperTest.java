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
    class FromRemoteClientWahlberechtigteDtoToAWerteModel {

        @Test
        void should_returnMappedAWerteModel_when_wahlberechtigteDtoisGiven() {
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
    class FromRemoteClientListOfWahlberechtigteDtoToListOfAWerteModel {

        @Test
        void should_returnMappedListOfAWerteModel_when_listOfWahlberechtigteDtoisGiven() {
            val wbDtoList = createListOfWahlberechtigteDTO();
            Assertions.assertThat(wbDtoList).size().isEqualTo(3);
            wbDtoList.forEach(wahlberechtigte -> Assertions.assertThat(wahlberechtigte).hasNoNullFieldsOrProperties());

            val result = unitUnderTest.fromRemoteClientListOfWahlberechtigteDtoToListOfAWerteModel(wbDtoList);

            val awm1 = new AWerteModel(new BezirkUndWahlID("wahlID", "wahlbezirkID1"), 2, 3L);
            val awm2 = new AWerteModel(new BezirkUndWahlID("wahlID", "wahlbezirkID2"), 3, 4L);
            val awm3 = new AWerteModel(new BezirkUndWahlID("wahlID", "wahlbezirkID3"), 4, 5L);
            val expectedAWerteList = List.of(awm1, awm2, awm3);

            Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedAWerteList);
        }
    }

    private List<WahlberechtigteDTO> createListOfWahlberechtigteDTO() {
        val wahlberechtigteDTO1 = new WahlberechtigteDTO();
        wahlberechtigteDTO1.setWahlbezirkID("wahlbezirkID1");
        wahlberechtigteDTO1.setWahlID("wahlID");
        wahlberechtigteDTO1.setA1(2L);
        wahlberechtigteDTO1.setA2(3L);
        wahlberechtigteDTO1.setA3(5L);
        val wahlberechtigteDTO2 = new WahlberechtigteDTO();
        wahlberechtigteDTO2.setWahlbezirkID("wahlbezirkID2");
        wahlberechtigteDTO2.setWahlID("wahlID");
        wahlberechtigteDTO2.setA1(3L);
        wahlberechtigteDTO2.setA2(4L);
        wahlberechtigteDTO2.setA3(7L);
        val wahlberechtigteDTO3 = new WahlberechtigteDTO();
        wahlberechtigteDTO3.setWahlbezirkID("wahlbezirkID3");
        wahlberechtigteDTO3.setWahlID("wahlID");
        wahlberechtigteDTO3.setA1(4L);
        wahlberechtigteDTO3.setA2(5L);
        wahlberechtigteDTO3.setA3(9L);
        return List.of(wahlberechtigteDTO1, wahlberechtigteDTO2, wahlberechtigteDTO3);
    }
}
