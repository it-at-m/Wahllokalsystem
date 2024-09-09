package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

class EreignisDTOMapperTest {

    private final EreignisDTOMapper unitUnderTest = Mappers.getMapper(EreignisDTOMapper.class);

    @Test
    void nullInNullOut() {
        Assertions.assertThat(unitUnderTest.toDTO(null)).isNull();
    }

    @Test
    void toDTO () {
        val ereignisModel = TestdataFactory.createEreignisModelWithData("wahlbezirkID", "beschreibung", LocalDateTime.now(), Ereignisart.VORFALL);
        val expectedEreignisDTO = TestdataFactory.createEreignisDTOFromModel(ereignisModel);

        val result = unitUnderTest.toDTO(ereignisModel);
        Assertions.assertThat(result).isEqualTo(expectedEreignisDTO);
    }

    @Test
    void toModel () {
        val wahlbezirkID = "wahlbezirkID";
        val ereignisWriteDTO = TestdataFactory.createEreignisWriteDTOWithData("beschreibung", LocalDateTime.now(), Ereignisart.VORFALL);
        val expectedEreignisModel = TestdataFactory.createEreignisModelFromWriteDTO(ereignisWriteDTO, wahlbezirkID);

        val result = unitUnderTest.toModel(wahlbezirkID, ereignisWriteDTO);
        Assertions.assertThat(result).isEqualTo(expectedEreignisModel);
    }
}
