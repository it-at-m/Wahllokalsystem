package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.Beschlussfassung;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.DSEKandidat;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.DSEStimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.DSEWahlvorschlag;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.KandidatID;
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
     class OfDSEStimmzettel {

      @Test
       void should_mapToModel_when_entityIsGiven() {
         val entityToMap = Instancio.create(DSEStimmzettel.class);

         val result = unitUnderTest.toModel(entityToMap);

         Assertions.assertThat(result.stimmzettelkennung())
             .isEqualTo(entityToMap.getId().getStimmzettelkennung());
         Assertions.assertThat(result.isValid()).isEqualTo(entityToMap.isValid());
         Assertions.assertThat(result.invalideVotes()).isEqualTo(entityToMap.getInvalideVotes());
       }
    }

    @Nested
     class OfDSEKandidat {

      @Test
       void should_mapToModel_when_entityIsGiven() {
         val kandidatId = Instancio.create(KandidatID.class);
         val entityToMap =
             new DSEKandidat(
                 kandidatId,
                 Instancio.create(DSEWahlvorschlag.class),
                 true,
                 1,
                 2,
                 3);

         val result = unitUnderTest.toModel(entityToMap);

         val expectedResult =
             new KandidatModel(
                 new KandidatIDModel(kandidatId.getKandidatID(), kandidatId.getNennungsNummer()),
                 entityToMap.isDiscarded(),
                 entityToMap.getVotesByVoter(),
                 entityToMap.getInvalidVotes(),
                 entityToMap.getVotesByWahlvorschlag());
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
         val ownerModel = Instancio.create(TeamBezirkUndWahlIDModel.class);
         val stimmzettelOfTeamModel =
             new StimmzettelOfTeamModel(
                 1,
                 true,
                 0,
                 StimmzettelGueltigkeitModel.VALID,
                 List.of(new BeschlussvormerkungModel("text")),
                 new BeschlussfassungModel(1, 0, "text"),
                 List.of());

         val result = unitUnderTest.toEntity(ownerModel, stimmzettelOfTeamModel);

         val expectedId =
             new StimmzettelID(
                 ownerModel.wahlbezirkID(), ownerModel.wahlID(), ownerModel.teamID(), 1);
         Assertions.assertThat(result.getId()).isEqualTo(expectedId);
         Assertions.assertThat(result.isValid()).isEqualTo(stimmzettelOfTeamModel.isValid());
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
         Assertions.assertThat(result.isDiscarded()).isEqualTo(modelToMap.isDiscarded());
         Assertions.assertThat(result.getVotesByVoter()).isEqualTo(modelToMap.votesByVoter());
       }
    }
  }
}
