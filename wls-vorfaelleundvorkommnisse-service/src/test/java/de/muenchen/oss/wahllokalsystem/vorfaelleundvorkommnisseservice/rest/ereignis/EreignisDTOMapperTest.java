package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisartModel;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class EreignisDTOMapperTest {

    private final EreignisDTOMapper unitUnderTest = Mappers.getMapper(EreignisDTOMapper.class);

    @Nested
    class ToDto {

        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toDTO(null)).isNull();
        }

        @Test
        void should_returnWahlbezirkEreignisseDTO_when_givenEreignisseModel() {
            val mockedListOfEreignisModel = List.of(
                    TestdataFactory.CreateEreignisModel.withEreignisart(EreignisartModel.VORFALL),
                    TestdataFactory.CreateEreignisModel.withEreignisart(EreignisartModel.VORKOMMNIS));
            val mockedEreignisseModel = TestdataFactory.CreateWahlbezirkEreignisseModel.withData("wahlbezirkID", false, false, mockedListOfEreignisModel);
            val expectedEreignisseDTO = TestdataFactory.CreateWahlbezirkEreignisseDto.fromModel(mockedEreignisseModel);

            val result = unitUnderTest.toDTO(mockedEreignisseModel);
            Assertions.assertThat(result).isEqualTo(expectedEreignisseDTO);
        }
    }

    @Nested
    class ToModel {

        @Test
        void should_returnEreignisseModel_when_givenEreignisseWriteDTO() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedListOfEreignisDto = List.of(
                    TestdataFactory.CreateEreignisDto.withData(),
                    TestdataFactory.CreateEreignisDto.withData());
            val mockedEreignisseWriteDTO = TestdataFactory.CreateEreignisseWriteDto.withData(mockedListOfEreignisDto);
            val expectedEreignisModel = TestdataFactory.CreateEreignisseWriteModel.fromDto(wahlbezirkID, mockedEreignisseWriteDTO);

            val result = unitUnderTest.toModel(wahlbezirkID, mockedEreignisseWriteDTO);
            Assertions.assertThat(result).isEqualTo(expectedEreignisModel);
        }
    }
}
