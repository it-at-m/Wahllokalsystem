package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.BeschlussfassungModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.KandidatIdModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.KandidatModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.StimmzettelGueltigkeitModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.StimmzettelOfTeamModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.SystemBeschlussgrundEnumModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.SystemBeschlussgrundModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.WahlvorschlagModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.WahlvorstandBeschlussgrundModel;
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
      val dtoToMap =
          new StimmzettelOfTeamDTO(1, 0, StimmzettelGueltigkeitDTO.VALID, null, null, null, null);

      val result = unitUnderTest.toModel(dtoToMap);

      Assertions.assertThat(result.stimmzettelkennung()).isEqualTo(dtoToMap.stimmzettelkennung());
    }

    @Test
    void should_mapCompleteModel_when_completeDTOIsGiven() {
      val dtoToMap = Instancio.create(StimmzettelOfTeamDTO.class);

      val result = unitUnderTest.toModel(dtoToMap);

      val expectedResult = createExpectedModel(dtoToMap);
      Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    private StimmzettelOfTeamModel createExpectedModel(final StimmzettelOfTeamDTO dto) {
      val expectedWahlvorstandBeschlussvorschlag =
          dto.wahlvorstandBeschlussvorschlag().stream()
              .map(
                  givenBeschlussvorschlag ->
                      new WahlvorstandBeschlussgrundModel(givenBeschlussvorschlag.text()))
              .toList();

      val expectedSystemBeschlussvorschlag =
          dto.systemBeschlussvorschlag().stream()
              .map(
                  givenSystemBeschlussvorschlag ->
                      new SystemBeschlussgrundModel(
                          SystemBeschlussgrundEnumModel.valueOf(
                              givenSystemBeschlussvorschlag.reason().name())))
              .toList();

      val expectedBeschlussfassung =
          new BeschlussfassungModel(
              dto.beschlussfassung().pro(),
              dto.beschlussfassung().contra(),
              dto.beschlussfassung().text());

      val expectedWahlvorschlaege =
          dto.wahlvorschlaege().stream()
              .map(
                  givenWahlvorschlag -> {
                    val expectedKandidaten =
                        givenWahlvorschlag.kandidaten().stream()
                            .map(
                                givenKandidat ->
                                    new KandidatModel(
                                        new KandidatIdModel(
                                            givenKandidat.id().kandidatID(),
                                            givenKandidat.id().nennungsNummer()),
                                        givenKandidat.discarded(),
                                        givenKandidat.votesByVoter(),
                                        givenKandidat.invalidVotes(),
                                        givenKandidat.votesByWahlvorschlag()))
                            .toList();

                    return new WahlvorschlagModel(
                        givenWahlvorschlag.wahlvorschlagID(),
                        givenWahlvorschlag.selected(),
                        expectedKandidaten);
                  })
              .toList();

      return new StimmzettelOfTeamModel(
          dto.stimmzettelkennung(),
          dto.invalideVotes(),
          StimmzettelGueltigkeitModel.valueOf(dto.gueltigkeit().name()),
          expectedWahlvorstandBeschlussvorschlag,
          expectedSystemBeschlussvorschlag,
          expectedBeschlussfassung,
          expectedWahlvorschlaege);
    }
  }

  @Nested
  class ToDTO {

    @Test
    void should_returnDTO_when_modelIsGiven() {
      val modelToMap =
          new StimmzettelOfTeamModel(
              1, 0, StimmzettelGueltigkeitModel.VALID, null, null, null, null);

      val result = unitUnderTest.toDTO(modelToMap);

      Assertions.assertThat(result.stimmzettelkennung()).isEqualTo(modelToMap.stimmzettelkennung());
    }

    @Test
    void should_mapCompleteDTO_when_completeModelIsGiven() {
      val modelToMap = Instancio.create(StimmzettelOfTeamModel.class);

      val result = unitUnderTest.toDTO(modelToMap);

      val expectedResult = createExpectedDTO(modelToMap);
      Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    private StimmzettelOfTeamDTO createExpectedDTO(final StimmzettelOfTeamModel model) {
      val expectedWahlvorstandBeschlussvorschlag =
          model.wahlvorstandBeschlussvorschlag().stream()
              .map(
                  givenBeschlussvorschlag ->
                      new WahlvorstandBeschlussgrundDTO(givenBeschlussvorschlag.text()))
              .toList();

      val expectedSystemBeschlussvorschlag =
          model.systemBeschlussvorschlag().stream()
              .map(
                  givenSystemBeschlussvorschlag ->
                      new SystemBeschlussgrundDTO(
                          SystemBeschlussgrundEnumDTO.valueOf(
                              givenSystemBeschlussvorschlag.reason().name())))
              .toList();

      val expectedBeschlussfassung =
          new BeschlussfassungDTO(
              model.beschlussfassung().pro(),
              model.beschlussfassung().contra(),
              model.beschlussfassung().text());

      val expectedWahlvorschlaege =
          model.wahlvorschlaege().stream()
              .map(
                  givenWahlvorschlag -> {
                    val expectedKandidaten =
                        givenWahlvorschlag.kandidaten().stream()
                            .map(
                                givenKandidat ->
                                    new KandidatDTO(
                                        new KandidatIdDTO(
                                            givenKandidat.id().kandidatID(),
                                            givenKandidat.id().nennungsNummer()),
                                        givenKandidat.discarded(),
                                        givenKandidat.votesByVoter(),
                                        givenKandidat.invalidVotes(),
                                        givenKandidat.votesByWahlvorschlag()))
                            .toList();

                    return new WahlvorschlagDTO(
                        givenWahlvorschlag.wahlvorschlagID(),
                        givenWahlvorschlag.selected(),
                        expectedKandidaten);
                  })
              .toList();

      return new StimmzettelOfTeamDTO(
          model.stimmzettelkennung(),
          model.invalideVotes(),
          StimmzettelGueltigkeitDTO.valueOf(model.gueltigkeit().name()),
          expectedWahlvorstandBeschlussvorschlag,
          expectedSystemBeschlussvorschlag,
          expectedBeschlussfassung,
          expectedWahlvorschlaege);
    }
  }
}
