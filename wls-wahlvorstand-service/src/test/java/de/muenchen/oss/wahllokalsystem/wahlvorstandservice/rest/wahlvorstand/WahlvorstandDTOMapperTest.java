package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.rest.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.utils.TestDataFactory;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class WahlvorstandDTOMapperTest {

    private final WahlvorstandDTOMapper unitUnderTest = Mappers.getMapper(WahlvorstandDTOMapper.class);

    @Nested
    class ToDTO {

        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toDTO(null)).isNull();
        }

        @Test
        void should_returnWahlvorstandDTO_when_givenWahlvorstandModel() {
            val mockedWahlvorstandModel = TestDataFactory.CreateWahlvorstandModel.withData();
            val expectedWahlvorstandDTO = TestDataFactory.CreateWahlvorstandDto.fromModel(mockedWahlvorstandModel);

            val result = unitUnderTest.toDTO(mockedWahlvorstandModel);
            Assertions.assertThat(result).isEqualTo(expectedWahlvorstandDTO);
        }
    }

    @Nested
    class ToModel {

        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toModel(null, null)).isNull();
        }

        @Test
        void should_returnWahlvorstandModel_when_givenWahlvorstandDTO() {
            val wahlbezirkID = "wahlbezirkID";
            val mockedWahlvorstandDTO = TestDataFactory.CreateWahlvorstandWriteDto.withData();
            val expectedWahlvorstandModel = TestDataFactory.CreateWahlvorstandModel.fromDto(wahlbezirkID, mockedWahlvorstandDTO);

            val result = unitUnderTest.toModel(wahlbezirkID, mockedWahlvorstandDTO);
            Assertions.assertThat(result).isEqualTo(expectedWahlvorstandModel);
        }
    }
}
