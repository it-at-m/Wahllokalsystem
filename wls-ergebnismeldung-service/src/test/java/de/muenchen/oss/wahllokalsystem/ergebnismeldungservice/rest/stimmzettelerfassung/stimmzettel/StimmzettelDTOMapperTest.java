package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.StimmzettelKandidatModel;
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
      val dtoToMap = Instancio.create(StimmzettelOfTeamDTO.class);

      val result = unitUnderTest.toModel(dtoToMap);

      val expectedWahlvorschlaegeOrdnungszahlen =
          dtoToMap.selectedWahlvorschlaegeOrdnungszahlen().stream().toList();
      val expectedKandidaten =
          dtoToMap.kandidaten().stream()
              .map(
                  dtoKandidat ->
                      new StimmzettelKandidatModel(
                          dtoKandidat.kandidatId(),
                          dtoKandidat.isDiscarded(),
                          dtoKandidat.votesByVoter()))
              .toList();
      val expectedResult =
          new StimmzettelOfTeamModel(
              dtoToMap.stimmzettelkennung(),
              expectedWahlvorschlaegeOrdnungszahlen,
              expectedKandidaten);

      Assertions.assertThat(result)
          .usingRecursiveComparison()
          .ignoringCollectionOrder()
          .isEqualTo(expectedResult);
    }
  }

  @Nested
  class ToDTO {

    @Test
    void should_returnDTO_when_modelIsGiven() {
      val modelToMap = Instancio.create(StimmzettelOfTeamModel.class);

      val result = unitUnderTest.toDTO(modelToMap);

      val expectedWahlvorschlaegeOrdnungszahlen =
          modelToMap.selectedWahlvorschlaegeOrdnungszahlen().stream().toList();
      val expectedKandidaten =
          modelToMap.kandidaten().stream()
              .map(
                  modelKandidat ->
                      new StimmzettelKandidatDTO(
                          modelKandidat.kandidatId(),
                          modelKandidat.isDiscarded(),
                          modelKandidat.votesByVoter()))
              .toList();
      val expectedResult =
          new StimmzettelOfTeamDTO(
              modelToMap.stimmzettelkennung(),
              expectedWahlvorschlaegeOrdnungszahlen,
              expectedKandidaten);

      Assertions.assertThat(result)
          .usingRecursiveComparison()
          .ignoringCollectionOrder()
          .isEqualTo(expectedResult);
    }
  }
}
