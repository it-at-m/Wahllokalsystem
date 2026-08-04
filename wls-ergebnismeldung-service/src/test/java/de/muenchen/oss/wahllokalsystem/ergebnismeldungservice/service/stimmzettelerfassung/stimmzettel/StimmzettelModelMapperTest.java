package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.KandidatID;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.Stimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelID;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import java.util.List;
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

        Assertions.assertThat(result.stimmzettelkennung())
            .isEqualTo(entityToMap.getId().getStimmzettelkennung());
        Assertions.assertThat(result.invalideVotes()).isEqualTo(entityToMap.getInvalideVotes());
      }
    }
  }

  @Nested
  class ToEntity {

    @Nested
    class OfStimmzettelOwnerAndStimmzettelOfTeamModel {

      @Test
      void should_returnEntity_when_modelsAreGiven() {
        val ownerModel = Instancio.create(TeamBezirkUndWahlIDModel.class);
        val stimmzettelOfTeamModel =
            new StimmzettelOfTeamModel(
                1,
                0,
                StimmzettelGueltigkeitModel.VALID,
                List.of(new BeschlussgrundModel("text")),
                new BeschlussfassungModel(1, 0, "text"),
                List.of());

        val result = unitUnderTest.toEntity(ownerModel, stimmzettelOfTeamModel);

        val expectedId =
            new StimmzettelID(
                ownerModel.wahlbezirkID(), ownerModel.wahlID(), ownerModel.teamID(), 1);
        Assertions.assertThat(result.getId()).isEqualTo(expectedId);
        Assertions.assertThat(result.getInvalideVotes())
            .isEqualTo(stimmzettelOfTeamModel.invalideVotes());
      }
    }

    @Nested
    class OfKandidatModel {

      @Test
      void should_returnEntity_when_modelIsGiven() {
        val kandidatIdModel = new KandidatIDModel("kid", 1);
        val modelToMap = new KandidatModel(kandidatIdModel, true, 1, 2, 3);

        val result = unitUnderTest.toEntity(modelToMap);

        val expectedId = new KandidatID("kid", 1);
        Assertions.assertThat(result.getId()).isEqualTo(expectedId);
        Assertions.assertThat(result.getVotesByVoter()).isEqualTo(modelToMap.votesByVoter());
      }
    }
  }
}
