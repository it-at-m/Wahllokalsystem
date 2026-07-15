package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.Stimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelID;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelKandidat;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class StimmzettelModelMapperTest {

  StimmzettelModelMapper unitUnderTest = Mappers.getMapper(StimmzettelModelMapper.class);

  @Nested
  class ToModel {

    @Nested
    class OfStimmzettel {

      @Test
      void should_mapToModel_when_entityIsGiven() {
        val entityToMap = Instancio.create(Stimmzettel.class);

        val result = unitUnderTest.toModel(entityToMap);

        val expectedListOfWahlvorschlaegeOrdnungszahlen =
            entityToMap.getSelectedWahlvorschlaegeOrdnungszahlen().stream().toList();
        val expectedListOfKandidaten =
            entityToMap.getKandidaten().stream()
                .map(
                    kandidatEntity ->
                        new StimmzettelKandidatModel(
                            kandidatEntity.getKandidatId(),
                            kandidatEntity.isDiscarded(),
                            kandidatEntity.getVotesByVoter()))
                .toList();
        val expectedResult =
            new StimmzettelOfTeamModel(
                entityToMap.getId().getStimmzettelkennung(),
                expectedListOfWahlvorschlaegeOrdnungszahlen,
                expectedListOfKandidaten);

        Assertions.assertThat(result)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedResult);
      }
    }

    @Nested
    class OfStimmzettelKandidat {

      @Test
      void should_mapToModel_when_entityIsGiven() {
        val entityToMap = Instancio.create(StimmzettelKandidat.class);

        val result = unitUnderTest.toModel(entityToMap);

        val expectedResult =
            new StimmzettelKandidatModel(
                entityToMap.getKandidatId(),
                entityToMap.isDiscarded(),
                entityToMap.getVotesByVoter());
        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
      }
    }
  }

  @Nested
  class ToEntity {

    @Nested
    class OfStimmzettelOwnerAndStimmzettelOfTeamModel {

      @Test
      void should_returnEntity_when_modelsAreGiven() {
        val ownerModel = Instancio.create(StimmzettelOwnerModel.class);
        val stimmzettelOfTeamModel = Instancio.create(StimmzettelOfTeamModel.class);

        val result = unitUnderTest.toEntity(ownerModel, stimmzettelOfTeamModel);

        val expectedListOfWahlvorschlaegeOrdnungszahlen =
            stimmzettelOfTeamModel.selectedWahlvorschlaegeOrdnungszahlen().stream().toList();
        val expectedListOfKandidaten =
            stimmzettelOfTeamModel.kandidaten().stream()
                .map(
                    kandidatModel ->
                        new StimmzettelKandidat(
                            kandidatModel.kandidatId(),
                            kandidatModel.isDiscarded(),
                            kandidatModel.votesByVoter()))
                .toList();
        val expectedResult =
            new Stimmzettel(
                new StimmzettelID(
                    ownerModel.wahlbezirkID(),
                    ownerModel.wahlID(),
                    ownerModel.teamID(),
                    stimmzettelOfTeamModel.stimmzettelkennung()),
                expectedListOfWahlvorschlaegeOrdnungszahlen,
                expectedListOfKandidaten);

        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
      }
    }

    @Nested
    class OfStimmzettelKandidatModel {

      @Test
      void should_returnEntity_when_modelIsGiven() {
        val modelToMap = Instancio.create(StimmzettelKandidatModel.class);

        val result = unitUnderTest.toEntity(modelToMap);

        val expectedResult =
            new StimmzettelKandidat(
                modelToMap.kandidatId(), modelToMap.isDiscarded(), modelToMap.votesByVoter());
        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
      }
    }
  }
}
