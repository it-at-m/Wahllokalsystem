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
    class ToDto {

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
            Assertions.assertThat(unitUnderTest.toModel(null)).isNull();
        }


        @Test
        void should_returnWahlvorstandModel_when_givenWahlvorstandDTO() {
            val mockedWahlvorstandDTO = TestDataFactory.CreateWahlvorstandDto.withData();
            val expectedWahlvorstandModel = TestDataFactory.CreateWahlvorstandModel.fromDto(mockedWahlvorstandDTO);

            val result = unitUnderTest.toModel(mockedWahlvorstandDTO);
            Assertions.assertThat(result).isEqualTo(expectedWahlvorstandModel);
        }
    }
}
