package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import static org.instancio.Select.field;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.Beschlussfassung;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.Beschlussgrund;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.Kandidat;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.KandidatId;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.Stimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelGueltigkeit;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelID;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.Wahlvorschlag;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import java.util.List;
import java.util.UUID;
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

        val expectedResult = createExpectedStimmzettel(entityToMap);
        Assertions.assertThat(result).isEqualTo(expectedResult);
      }

      private StimmzettelOfTeamModel createExpectedStimmzettel(final Stimmzettel stimmzettel) {
        val expectedBeschlussvorschlag =
            stimmzettel.getBeschlussvorschlag().stream()
                .map(
                    givenBeschlussvorschlag ->
                        new BeschlussgrundModel(givenBeschlussvorschlag.getText()))
                .toList();
        val expectedWahlvorschlaege =
            stimmzettel.getWahlvorschlaege().stream()
                .map(
                    givenWahlvorschlag -> {
                      val expectedKandidaten =
                          givenWahlvorschlag.getKandidaten().stream()
                              .map(
                                  givenKandidaten ->
                                      new KandidatModel(
                                          new KandidatIdModel(
                                              givenKandidaten.getId().getKandidatID(),
                                              givenKandidaten.getId().getNennungsNummer()),
                                          givenKandidaten.isDiscarded(),
                                          givenKandidaten.getVotesByVoter(),
                                          givenKandidaten.getInvalidVotes(),
                                          givenKandidaten.getVotesByWahlvorschlag()))
                              .toList();

                      return new WahlvorschlagModel(
                          givenWahlvorschlag.getWahlvorschlagID(),
                          givenWahlvorschlag.isSelected(),
                          expectedKandidaten);
                    })
                .toList();
        return new StimmzettelOfTeamModel(
            stimmzettel.getId().getStimmzettelkennung(),
            stimmzettel.getInvalideVotes(),
            StimmzettelGueltigkeitModel.valueOf(stimmzettel.getGueltigkeit().name()),
            expectedBeschlussvorschlag,
            new BeschlussfassungModel(
                stimmzettel.getBeschlussfassung().getPro(),
                stimmzettel.getBeschlussfassung().getContra(),
                stimmzettel.getBeschlussfassung().getText()),
            expectedWahlvorschlaege);
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
        val beschlussvorschlag1 = Instancio.create(BeschlussgrundModel.class);
        val beschlussvorschlag2 = Instancio.create(BeschlussgrundModel.class);
        val beschlussvorschlag3 = Instancio.create(BeschlussgrundModel.class);
        val stimmzettelOfTeamModel =
            Instancio.of(StimmzettelOfTeamModel.class)
                .set(
                    field(StimmzettelOfTeamModel::beschlussvorschlag),
                    List.of(beschlussvorschlag1, beschlussvorschlag2, beschlussvorschlag3))
                .create();

        val result = unitUnderTest.toEntity(ownerModel, stimmzettelOfTeamModel);

        val expectedResult = createExpectedStimmzettel(stimmzettelOfTeamModel, ownerModel);
        Assertions.assertThat(result)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .ignoringFieldsOfTypes(UUID.class)
            .isEqualTo(expectedResult);
      }

      private Stimmzettel createExpectedStimmzettel(
          final StimmzettelOfTeamModel stimmzettel, final TeamBezirkUndWahlIDModel ownerModel) {
        val expectedResult = new Stimmzettel();

        val expectedBeschlussvorschlag =
            stimmzettel.beschlussvorschlag().stream()
                .map(
                    givenBeschlussvorschlag ->
                        createExpectedBeschlussgrund(givenBeschlussvorschlag, expectedResult))
                .toList();
        val expectedBeschlussfassung =
            createExpectedBeschlussfassung(stimmzettel.beschlussfassung());
        val expectedWahlvorschlaege =
            stimmzettel.wahlvorschlaege().stream()
                .map(
                    givenWahlvorschlaag ->
                        createExpectedWahlvorschlag(givenWahlvorschlaag, expectedResult))
                .toList();

        expectedResult.setId(
            new StimmzettelID(
                ownerModel.wahlbezirkID(),
                ownerModel.wahlID(),
                ownerModel.teamID(),
                stimmzettel.stimmzettelkennung()));
        expectedResult.setInvalideVotes(stimmzettel.invalideVotes());
        expectedResult.setGueltigkeit(
            StimmzettelGueltigkeit.valueOf(stimmzettel.gueltigkeit().name()));
        expectedResult.setBeschlussvorschlag(expectedBeschlussvorschlag);
        expectedResult.setBeschlussfassung(expectedBeschlussfassung);
        expectedResult.setWahlvorschlaege(expectedWahlvorschlaege);

        return expectedResult;
      }

      private Beschlussgrund createExpectedBeschlussgrund(
          final BeschlussgrundModel beschlussgrund, final Stimmzettel stimmzettel) {
        val expectedBeschlussgrund = new Beschlussgrund();

        expectedBeschlussgrund.setText(beschlussgrund.text());
        expectedBeschlussgrund.setStimmzettel(stimmzettel);

        return expectedBeschlussgrund;
      }

      private Beschlussfassung createExpectedBeschlussfassung(
          final BeschlussfassungModel beschlussfassung) {
        return Instancio.of(Beschlussfassung.class)
            .set(field(Beschlussfassung::getContra), beschlussfassung.contra())
            .set(field(Beschlussfassung::getPro), beschlussfassung.pro())
            .set(field(Beschlussfassung::getText), beschlussfassung.text())
            .create();
      }

      private Wahlvorschlag createExpectedWahlvorschlag(
          final WahlvorschlagModel wahlvorschlag, final Stimmzettel stimmzettel) {
        val expectedWahlvorschlag = new Wahlvorschlag();

        val expectedKandidaten =
            wahlvorschlag.kandidaten().stream()
                .map(givenKandidat -> createdExpectedKandidat(givenKandidat, expectedWahlvorschlag))
                .toList();

        expectedWahlvorschlag.setStimmzettel(stimmzettel);
        expectedWahlvorschlag.setWahlvorschlagID(wahlvorschlag.wahlvorschlagID());
        expectedWahlvorschlag.setSelected(wahlvorschlag.selected());
        expectedWahlvorschlag.setKandidaten(expectedKandidaten);

        return expectedWahlvorschlag;
      }

      private Kandidat createdExpectedKandidat(
          final KandidatModel kandidat, final Wahlvorschlag wahlvorschlag) {
        val expectedResult = new Kandidat();

        expectedResult.setId(
            new KandidatId(kandidat.id().kandidatID(), kandidat.id().nennungsNummer()));
        expectedResult.setWahlvorschlag(wahlvorschlag);
        expectedResult.setDiscarded(kandidat.discarded());
        expectedResult.setVotesByVoter(kandidat.votesByVoter());
        expectedResult.setInvalidVotes(kandidat.invalidVotes());
        expectedResult.setVotesByWahlvorschlag(kandidat.votesByWahlvorschlag());

        return expectedResult;
      }
    }

    @Nested
    class OfKandidatModel {

      @Test
      void should_returnEntity_when_modelIsGiven() {
        val modelToMap = Instancio.create(KandidatModel.class);

        val result = unitUnderTest.toEntity(modelToMap);

        val expectedResult = createExpectedKandidat(modelToMap);
        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
      }

      private Kandidat createExpectedKandidat(final KandidatModel kandidat) {
        val expectedKandidat = new Kandidat();

        expectedKandidat.setId(
            new KandidatId(kandidat.id().kandidatID(), kandidat.id().nennungsNummer()));
        expectedKandidat.setDiscarded(kandidat.discarded());
        expectedKandidat.setVotesByVoter(kandidat.votesByVoter());
        expectedKandidat.setInvalidVotes(kandidat.invalidVotes());
        expectedKandidat.setVotesByWahlvorschlag(kandidat.votesByWahlvorschlag());

        return expectedKandidat;
      }
    }
  }
}
