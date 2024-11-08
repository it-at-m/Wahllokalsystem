package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.utils.TestDataFactory;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class WahlvorstandModelMapperTest {

    private final WahlvorstandModelMapper unitUnderTest = Mappers.getMapper(WahlvorstandModelMapper.class);

    @Nested
    class ToModel {

        @Test
        void should_returnWahlvorstandModel_when_givenWahlvorstandEntity() {
            val mockedWahlvorstandEntity = TestDataFactory.CreateWahlvorstandEntity.withData();
            val expectedWahlvorstandModel = TestDataFactory.CreateWahlvorstandModel.fromEntity(mockedWahlvorstandEntity);

            val result = unitUnderTest.toModel(mockedWahlvorstandEntity);
            Assertions.assertThat(result).isEqualTo(expectedWahlvorstandModel);
        }
    }

    @Nested
    class ToEntity {

        @Nested
        class ToWahlvorstandsmitgliedEntity {

            @Test
            void should_returnWahlvorstandsmitgliedEntity_when_givenWahlvorstandsmitgliedModel() {
                val mockedWahlvorstandsmitgliedModel = TestDataFactory.CreateWahlvorstandsmitgliedModel.withData();
                val expectedWahlvorstandsmitgliedEntity = TestDataFactory.CreateWahlvorstandsmitgliedEntity.fromModel(mockedWahlvorstandsmitgliedModel);

                val result = unitUnderTest.toEntity(mockedWahlvorstandsmitgliedModel);
                Assertions.assertThat(result).isEqualTo(expectedWahlvorstandsmitgliedEntity);
            }
        }

        @Nested
        class ToWahlvorstandEntity {

            @Test
            void should_returnWahlvorstandEntity_when_givenWahlvorstandModel() {
                val mockedWahlvorstandModel = TestDataFactory.CreateWahlvorstandModel.withData();
                val expectedWahlvorstandEntity = TestDataFactory.CreateWahlvorstandEntity.fromModel(mockedWahlvorstandModel);

                val result = unitUnderTest.toEntity(mockedWahlvorstandModel);
                Assertions.assertThat(result).isEqualTo(expectedWahlvorstandEntity);
            }
        }
    }
}
