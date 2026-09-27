package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankDiscardedKandidat;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankKandidatWithInvalidVote;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankKandidatWithSingleVoteByVoter;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankKandidatWithSingleVoteByWahlvorschlag;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankNonSelectedWahlvorschlagModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankSelectedWahlvorschlagModel;
import static org.instancio.Select.field;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.Wahlvorschlag;
import java.util.List;
import org.instancio.Instancio;
import org.instancio.Model;

public class StimmzettelRepositoryTestWahlvorschlagModels {

  public static final Model<List<Wahlvorschlag>>
      singleWahlvorschlagModelWithOnlyReststimmenKandidaten =
          Instancio.ofList(createBlankSelectedWahlvorschlagModel("onlyReststimmen"))
              .size(1)
              .set(field(Wahlvorschlag::isSelected), true)
              .supply(
                  field(Wahlvorschlag::getKandidaten),
                  () ->
                      List.of(
                          createBlankKandidatWithSingleVoteByWahlvorschlag("k2", 1),
                          createBlankKandidatWithSingleVoteByWahlvorschlag("k2", 2)))
              .toModel();

  public static final Model<List<Wahlvorschlag>> singleWahlvorschlagModelWithMultipleStreichungen =
      Instancio.ofList(createBlankNonSelectedWahlvorschlagModel("wvMultipleStreichung"))
          .size(1)
          .supply(
              field(Wahlvorschlag::getKandidaten),
              () ->
                  List.of(
                      createBlankDiscardedKandidat("k2", 1), createBlankDiscardedKandidat("k2", 2)))
          .toModel();

  public static final Model<List<Wahlvorschlag>> singleWahlvorschlagModelWithSingleStreichungen =
      Instancio.ofList(createBlankNonSelectedWahlvorschlagModel("wvStreichung"))
          .size(1)
          .supply(
              field(Wahlvorschlag::getKandidaten),
              () -> List.of(createBlankDiscardedKandidat("k2", 1)))
          .toModel();

  public static Model<List<Wahlvorschlag>>
      createSingleWahlvorschlagModelWithStreichungAndEinzelStimme(final String wahlvorschlagID) {
    return Instancio.ofList(createBlankNonSelectedWahlvorschlagModel(wahlvorschlagID))
        .size(1)
        .supply(
            field(Wahlvorschlag::getKandidaten),
            () ->
                List.of(
                    createBlankDiscardedKandidat("k1", 1),
                    createBlankKandidatWithSingleVoteByVoter("k2", 1)))
        .toModel();
  }

  public static final Model<List<Wahlvorschlag>>
      singleWahlvorschlagModelWithStreichungAndEinzelStimme =
          createSingleWahlvorschlagModelWithStreichungAndEinzelStimme("wvStreichung+Einzelstimme");

  public static final Model<List<Wahlvorschlag>>
      singleWahlvorschlagModelWithStreichungAndReststimme =
          Instancio.ofList(createBlankNonSelectedWahlvorschlagModel("wvStreichung+Reststimme"))
              .size(1)
              .supply(
                  field(Wahlvorschlag::getKandidaten),
                  () ->
                      List.of(
                          createBlankDiscardedKandidat("k1", 1),
                          createBlankKandidatWithSingleVoteByWahlvorschlag("k2", 1)))
              .toModel();

  public static Model<List<Wahlvorschlag>> createSingleWahlvorschlagModelWithReststimme(
      final String wahlvorschlagID) {
    return Instancio.ofList(createBlankNonSelectedWahlvorschlagModel(wahlvorschlagID))
        .size(1)
        .set(field(Wahlvorschlag::isSelected), true)
        .supply(
            field(Wahlvorschlag::getKandidaten),
            () -> List.of(createBlankKandidatWithSingleVoteByWahlvorschlag("k2", 1)))
        .toModel();
  }

  public static final Model<List<Wahlvorschlag>> singleWahlvorschlagModelWithReststimme =
      createSingleWahlvorschlagModelWithReststimme("wvReststimme");

  public static Model<List<Wahlvorschlag>>
      createSingleWahlvorschlagModelWithReststimmeAndEinzelstimme(final String wahlvorschlagID) {
    return Instancio.ofList(createBlankNonSelectedWahlvorschlagModel(wahlvorschlagID))
        .size(1)
        .set(field(Wahlvorschlag::isSelected), true)
        .supply(
            field(Wahlvorschlag::getKandidaten),
            () ->
                List.of(
                    createBlankKandidatWithSingleVoteByWahlvorschlag("k1", 1),
                    createBlankKandidatWithSingleVoteByVoter("k2", 1)))
        .toModel();
  }

  public static final Model<List<Wahlvorschlag>>
      singleWahlvorschlagModelWithReststimmeAndEinzelstimme =
          createSingleWahlvorschlagModelWithReststimmeAndEinzelstimme("wvReststimme+Einzelstimme");

  public static Model<List<Wahlvorschlag>> createSingleWahlvorschlagModelWithEinzelstimme(
      final String wahlvorschlagID) {
    return Instancio.ofList(createBlankNonSelectedWahlvorschlagModel(wahlvorschlagID))
        .size(1)
        .supply(
            field(Wahlvorschlag::getKandidaten),
            () -> List.of(createBlankKandidatWithSingleVoteByVoter("k1", 1)))
        .toModel();
  }

  public static final Model<List<Wahlvorschlag>> singleWahlvorschlagModelWithEinzelstimme =
      createSingleWahlvorschlagModelWithEinzelstimme("wvEinzelstimme");

  public static final Model<List<Wahlvorschlag>> singleWahlvorschlagModelWithSingleInvalideVote =
      Instancio.ofList(createBlankNonSelectedWahlvorschlagModel("wvInvalidVote"))
          .size(1)
          .supply(
              field(Wahlvorschlag::getKandidaten),
              () -> List.of(createBlankKandidatWithInvalidVote("k1", 1)))
          .toModel();

  public static final Model<List<Wahlvorschlag>>
      singleWahlvorschlagModelWithInvalideVoteAndReststimme =
          Instancio.ofList(createBlankNonSelectedWahlvorschlagModel("wvInvalidVote+Reststimme"))
              .size(1)
              .supply(
                  field(Wahlvorschlag::getKandidaten),
                  () ->
                      List.of(
                          createBlankKandidatWithInvalidVote("k1", 1),
                          createBlankKandidatWithSingleVoteByWahlvorschlag("k2", 1)))
              .toModel();
}
