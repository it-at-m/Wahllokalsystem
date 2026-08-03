package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.KandidatIDModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.KandidatModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.StimmzettelGueltigkeitModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.StimmzettelOfTeamModel;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class StimmzettelDTOMapperTest {

  StimmzettelDTOMapper unitUnderTest = Mappers.getMapper(StimmzettelDTOMapper.class);

  @Nested
  class ToModel {

    @Test
    void should_returnModel_when_dtoIsGiven() {
       val dtoToMap = new StimmzettelOfTeamDTO(1, true, 0, StimmzettelGueltigkeitDTO.VALID, null, null, null);

       val result = unitUnderTest.toModel(dtoToMap);

       Assertions.assertThat(result.stimmzettelkennung()).isEqualTo(dtoToMap.stimmzettelkennung());
       Assertions.assertThat(result.valid()).isEqualTo(dtoToMap.valid());
    }
  }

  @Nested
  class ToDTO {

    @Test
    void should_returnDTO_when_modelIsGiven() {
       val modelToMap =
           new StimmzettelOfTeamModel(
               1, true, 0, StimmzettelGueltigkeitModel.VALID, null, null, null);

       val result = unitUnderTest.toDTO(modelToMap);

       Assertions.assertThat(result.stimmzettelkennung()).isEqualTo(modelToMap.stimmzettelkennung());
    }
  }
}
