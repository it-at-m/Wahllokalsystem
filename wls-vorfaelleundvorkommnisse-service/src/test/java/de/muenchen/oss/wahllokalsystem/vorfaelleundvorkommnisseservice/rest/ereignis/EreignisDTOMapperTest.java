package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModel;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class EreignisDTOMapperTest {

    private final EreignisDTOMapper unitUnderTest = Mappers.getMapper(EreignisDTOMapper.class);

    @Test
    void nullInNullOut() {
        Assertions.assertThat(unitUnderTest.toDTO(null)).isNull();
    }

    @Test
    void should_return_WahlbezirkEreignisseDTO_when_given_EreignisseModel() {
        List<EreignisModel> listOfEreignisModel = new ArrayList<>();
        listOfEreignisModel.add(TestdataFactory.createEreignisModelWithData("beschreibung", LocalDateTime.now(), Ereignisart.VORFALL));
        listOfEreignisModel.add(TestdataFactory.createEreignisModelWithData("beschreibung2", LocalDateTime.now(), Ereignisart.VORKOMMNIS));
        val ereignisseModel = TestdataFactory.createEreignisseModelWithData("wahlbezirkID", false, false, listOfEreignisModel);
        val expectedEreignisseDTO = TestdataFactory.createWahlbezirkEreignisseDTOFromModel(ereignisseModel);

        val result = unitUnderTest.toDTO(ereignisseModel);
        Assertions.assertThat(result).isEqualTo(expectedEreignisseDTO);
    }

    @Test
    void should_return_EreignisseModel_when_given_EreignisseWriteDTO() {
        val wahlbezirkID = "wahlbezirkID";
        List<EreignisDTO> listOfEreignisDto = new ArrayList<>();
        listOfEreignisDto.add(TestdataFactory.createEreignisDtoWithData("beschreibung", LocalDateTime.now(), Ereignisart.VORFALL));
        listOfEreignisDto.add(TestdataFactory.createEreignisDtoWithData("beschreibung2", LocalDateTime.now(), Ereignisart.VORKOMMNIS));
        val ereignisseWriteDTO = TestdataFactory.createEreignisseWriteDTOWithData(listOfEreignisDto);
        val expectedEreignisModel = TestdataFactory.createEreignisseWriteModelFromDto(wahlbezirkID, ereignisseWriteDTO);

        val result = unitUnderTest.toModel(wahlbezirkID, ereignisseWriteDTO);
        Assertions.assertThat(result).isEqualTo(expectedEreignisModel);
    }
}
