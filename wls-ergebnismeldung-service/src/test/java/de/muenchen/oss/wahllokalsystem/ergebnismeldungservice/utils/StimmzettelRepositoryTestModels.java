package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createEmptyDiscardedKandidat;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createEmptyKandidatWithSingleVoteByVoter;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createEmptyKandidatWithSingleVoteByWahlvorschlag;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createEmptySelectedWahlvorschlag;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createEmptyWahlvorschlag;
import static org.instancio.Select.field;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.Wahlvorschlag;
import java.util.List;
import org.instancio.Instancio;
import org.instancio.Model;

public class StimmzettelRepositoryTestModels {

  public static final Model<List<Wahlvorschlag>>
      singleWahlvorschlagWithOnlyReststimmenKandidatenModel =
          Instancio.ofList(createEmptySelectedWahlvorschlag("onlyReststimmen"))
              .size(1)
              .set(field(Wahlvorschlag::isSelected), true)
              .supply(
                  field(Wahlvorschlag::getKandidaten),
                  () ->
                      List.of(
                          createEmptyKandidatWithSingleVoteByWahlvorschlag("k2", 1),
                          createEmptyKandidatWithSingleVoteByWahlvorschlag("k2", 2)))
              .toModel();

  public static final Model<List<Wahlvorschlag>> singleWahlvorschlagWithMultipleStreichungenModel =
      Instancio.ofList(createEmptyWahlvorschlag("wvMultipleStreichung"))
          .size(1)
          .supply(
              field(Wahlvorschlag::getKandidaten),
              () ->
                  List.of(
                      createEmptyDiscardedKandidat("k2", 1), createEmptyDiscardedKandidat("k2", 2)))
          .toModel();

  public static final Model<List<Wahlvorschlag>> singleWahlvorschlagWithSingleStreichungenModel =
      Instancio.ofList(createEmptyWahlvorschlag("wvStreichung"))
          .size(1)
          .supply(
              field(Wahlvorschlag::getKandidaten),
              () -> List.of(createEmptyDiscardedKandidat("k2", 1)))
          .toModel();

  public static Model<List<Wahlvorschlag>>
      createSingleWahlvorschlagWithStreichungAndEinzelStimmeModel(final String wahlvorschlagID) {
    return Instancio.ofList(createEmptyWahlvorschlag(wahlvorschlagID))
        .size(1)
        .supply(
            field(Wahlvorschlag::getKandidaten),
            () ->
                List.of(
                    createEmptyDiscardedKandidat("k1", 1),
                    createEmptyKandidatWithSingleVoteByVoter("k2", 1)))
        .toModel();
  }

  public static final Model<List<Wahlvorschlag>>
      singleWahlvorschlagWithStreichungAndEinzelStimmeModel =
          createSingleWahlvorschlagWithStreichungAndEinzelStimmeModel("wvStreichung+Einzelstimme");

  public static final Model<List<Wahlvorschlag>>
      singleWahlvorschlagWithStreichungAndReststimmeModel =
          Instancio.ofList(createEmptyWahlvorschlag("wvStreichung+Reststimme"))
              .size(1)
              .supply(
                  field(Wahlvorschlag::getKandidaten),
                  () ->
                      List.of(
                          createEmptyDiscardedKandidat("k1", 1),
                          createEmptyKandidatWithSingleVoteByWahlvorschlag("k2", 1)))
              .toModel();

  public static Model<List<Wahlvorschlag>> createSingleWahlvorschlagWithReststimmeModel(
      final String wahlvorschlagID) {
    return Instancio.ofList(createEmptyWahlvorschlag(wahlvorschlagID))
        .size(1)
        .set(field(Wahlvorschlag::isSelected), true)
        .supply(
            field(Wahlvorschlag::getKandidaten),
            () -> List.of(createEmptyKandidatWithSingleVoteByWahlvorschlag("k2", 1)))
        .toModel();
  }

  public static final Model<List<Wahlvorschlag>> singleWahlvorschlagWithReststimmeModel =
      createSingleWahlvorschlagWithReststimmeModel("wvReststimme");

  public static Model<List<Wahlvorschlag>>
      createSingleWahlvorschlagWithReststimmeAndEinzelstimmeModel(final String wahlvorschlagID) {
    return Instancio.ofList(createEmptyWahlvorschlag(wahlvorschlagID))
        .size(1)
        .set(field(Wahlvorschlag::isSelected), true)
        .supply(
            field(Wahlvorschlag::getKandidaten),
            () ->
                List.of(
                    createEmptyKandidatWithSingleVoteByWahlvorschlag("k1", 1),
                    createEmptyKandidatWithSingleVoteByVoter("k2", 1)))
        .toModel();
  }

  public static final Model<List<Wahlvorschlag>>
      singleWahlvorschlagWithReststimmeAndEinzelstimmeModel =
          createSingleWahlvorschlagWithReststimmeAndEinzelstimmeModel("wvReststimme+Einzelstimme");

  public static Model<List<Wahlvorschlag>> createSingleWahlvorschlagWithEinzelstimmeModel(
      final String wahlvorschlagID) {
    return Instancio.ofList(createEmptyWahlvorschlag(wahlvorschlagID))
        .size(1)
        .supply(
            field(Wahlvorschlag::getKandidaten),
            () -> List.of(createEmptyKandidatWithSingleVoteByVoter("k1", 1)))
        .toModel();
  }

  public static final Model<List<Wahlvorschlag>> singleWahlvorschlagWithEinzelstimmeModel =
      createSingleWahlvorschlagWithEinzelstimmeModel("wvEinzelstimme");
}
