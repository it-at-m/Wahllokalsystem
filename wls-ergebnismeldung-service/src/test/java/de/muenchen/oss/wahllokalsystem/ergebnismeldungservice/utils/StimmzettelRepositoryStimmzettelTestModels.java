package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createEmptyInvalidStimmzettelModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createEmptyKandidatWithSingleVoteByVoter;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createEmptyKandidatWithSingleVoteByWahlvorschlag;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createEmptyValidStimmzettelModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createEmptyWahlvorschlag;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagWithEinzelstimmeModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagWithMultipleStreichungenModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagWithOnlyReststimmenKandidatenModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagWithReststimmeAndEinzelstimmeModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagWithSingleStreichungenModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagWithStreichungAndEinzelStimmeModel;
import static org.instancio.Select.field;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.Stimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.Wahlvorschlag;
import java.util.List;
import org.instancio.Instancio;
import org.instancio.Model;

public class StimmzettelRepositoryStimmzettelTestModels {

  public static Model<Stimmzettel>
      createValidStimmzettelModelWithSingleWahlvorschlagWithOnlyReststimmen(
          final String wahlID,
          final String wahlbezirkID,
          final String teamID,
          final int stimzettelkennung) {
    return Instancio.of(
            createEmptyInvalidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .setModel(
            field(Stimmzettel::getWahlvorschlaege),
            singleWahlvorschlagWithOnlyReststimmenKandidatenModel)
        .toModel();
  }

  public static Model<Stimmzettel>
      createInvalidStimmzettelModelWithSingleWahlvorschlagWithEinzelstimme(
          final String wahlID,
          final String wahlbezirkID,
          final String teamID,
          final int stimzettelkennung) {
    return Instancio.of(
            createEmptyInvalidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .setModel(field(Stimmzettel::getWahlvorschlaege), singleWahlvorschlagWithEinzelstimmeModel)
        .toModel();
  }

  public static Model<Stimmzettel>
      createValidStimmzettelModelWithSingleWahlvorschlagWithEinzelstimme(
          final String wahlID,
          final String wahlbezirkID,
          final String teamID,
          final int stimzettelkennung) {
    return Instancio.of(
            createEmptyValidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .setModel(field(Stimmzettel::getWahlvorschlaege), singleWahlvorschlagWithEinzelstimmeModel)
        .toModel();
  }

  public static Model<Stimmzettel>
      createInvalidStimmzettelModelWithSingleWahlvorschlagWithMultipleStreichungen(
          final String wahlID,
          final String wahlbezirkID,
          final String teamID,
          final int stimzettelkennung) {
    return Instancio.of(
            createEmptyInvalidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .setModel(
            field(Stimmzettel::getWahlvorschlaege),
            singleWahlvorschlagWithMultipleStreichungenModel)
        .toModel();
  }

  public static Model<Stimmzettel>
      createValidStimmzettelModelWithSingleWahlvorschlagWithMultipleStreichungen(
          final String wahlID,
          final String wahlbezirkID,
          final String teamID,
          final int stimzettelkennung) {
    return Instancio.of(
            createEmptyValidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .setModel(
            field(Stimmzettel::getWahlvorschlaege),
            singleWahlvorschlagWithMultipleStreichungenModel)
        .toModel();
  }

  public static Model<Stimmzettel>
      createValidStimmzettelModelWithSingleWahlvorschlagWithSingleStreichung(
          final String wahlID,
          final String wahlbezirkID,
          final String teamID,
          final int stimzettelkennung) {
    return Instancio.of(
            createEmptyValidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .setModel(
            field(Stimmzettel::getWahlvorschlaege), singleWahlvorschlagWithSingleStreichungenModel)
        .toModel();
  }

  public static Model<Stimmzettel>
      createInvalidStimmzettelModelWithSingleWahlvorschlagWithStreichungAndEinzelstimme(
          final String wahlID,
          final String wahlbezirkID,
          final String teamID,
          final int stimzettelkennung) {
    return Instancio.of(
            createEmptyInvalidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .setModel(
            field(Stimmzettel::getWahlvorschlaege),
            singleWahlvorschlagWithStreichungAndEinzelStimmeModel)
        .toModel();
  }

  public static Model<Stimmzettel>
      createValidStimmzettelModelWithSingleWahlvorschlagWithStreichungAndEinzelstimme(
          final String wahlID,
          final String wahlbezirkID,
          final String teamID,
          final int stimzettelkennung) {
    return Instancio.of(
            createEmptyValidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .setModel(
            field(Stimmzettel::getWahlvorschlaege),
            singleWahlvorschlagWithStreichungAndEinzelStimmeModel)
        .toModel();
  }

  public static Model<Stimmzettel>
      createValidStimmzettelModelWithSingleWahlvorschlagWithReststimmeAndEinzelstimme(
          final String wahlID,
          final String wahlbezirkID,
          final String teamID,
          final int stimzettelkennung) {
    return Instancio.of(
            createEmptyValidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .setModel(
            field(Stimmzettel::getWahlvorschlaege),
            singleWahlvorschlagWithReststimmeAndEinzelstimmeModel)
        .toModel();
  }

  public static Model<Stimmzettel>
      createInvalidStimmzettelModelWith2WahlvorschlaegenEachWithEinzelstimme(
          final String wahlID,
          final String wahlbezirkID,
          final String teamID,
          final int stimzettelkennung) {
    return Instancio.of(
            createEmptyInvalidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .supply(
            field(Stimmzettel::getWahlvorschlaege),
            () ->
                List.of(
                    Instancio.of(createEmptyWahlvorschlag("wv1"))
                        .supply(
                            field(Wahlvorschlag::getKandidaten),
                            () -> List.of(createEmptyKandidatWithSingleVoteByVoter("k11", 1)))
                        .create(),
                    Instancio.of(createEmptyWahlvorschlag("wv2"))
                        .supply(
                            field(Wahlvorschlag::getKandidaten),
                            () -> List.of(createEmptyKandidatWithSingleVoteByVoter("k21", 1)))
                        .create()))
        .toModel();
  }

  public static Model<Stimmzettel>
      createValidStimmzettelModelWith2WahlvorschlaegenEachWithEinzelstimme(
          final String wahlID,
          final String wahlbezirkID,
          final String teamID,
          final int stimzettelkennung) {
    return Instancio.of(
            createEmptyValidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .supply(
            field(Stimmzettel::getWahlvorschlaege),
            () ->
                List.of(
                    Instancio.of(createEmptyWahlvorschlag("wv1"))
                        .supply(
                            field(Wahlvorschlag::getKandidaten),
                            () -> List.of(createEmptyKandidatWithSingleVoteByVoter("k11", 1)))
                        .create(),
                    Instancio.of(createEmptyWahlvorschlag("wv2"))
                        .supply(
                            field(Wahlvorschlag::getKandidaten),
                            () -> List.of(createEmptyKandidatWithSingleVoteByVoter("k21", 1)))
                        .create()))
        .toModel();
  }

  public static Model<Stimmzettel>
      createInvalidStimmzettelModelWith2WahlvorschlaegenEachWithReststimme(
          final String wahlID,
          final String wahlbezirkID,
          final String teamID,
          final int stimzettelkennung) {
    return Instancio.of(
            createEmptyInvalidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .supply(
            field(Stimmzettel::getWahlvorschlaege),
            () ->
                List.of(
                    Instancio.of(createEmptyWahlvorschlag("wv1"))
                        .supply(
                            field(Wahlvorschlag::getKandidaten),
                            () ->
                                List.of(createEmptyKandidatWithSingleVoteByWahlvorschlag("k11", 1)))
                        .create(),
                    Instancio.of(createEmptyWahlvorschlag("wv2"))
                        .supply(
                            field(Wahlvorschlag::getKandidaten),
                            () ->
                                List.of(createEmptyKandidatWithSingleVoteByWahlvorschlag("k21", 1)))
                        .create()))
        .toModel();
  }

  public static Model<Stimmzettel>
      createValidStimmzettelModelWith2WahlvorschlaegenEachWithReststimme(
          final String wahlID,
          final String wahlbezirkID,
          final String teamID,
          final int stimzettelkennung) {
    return Instancio.of(
            createEmptyValidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .supply(
            field(Stimmzettel::getWahlvorschlaege),
            () ->
                List.of(
                    Instancio.of(createEmptyWahlvorschlag("wv1"))
                        .supply(
                            field(Wahlvorschlag::getKandidaten),
                            () ->
                                List.of(createEmptyKandidatWithSingleVoteByWahlvorschlag("k11", 1)))
                        .create(),
                    Instancio.of(createEmptyWahlvorschlag("wv2"))
                        .supply(
                            field(Wahlvorschlag::getKandidaten),
                            () ->
                                List.of(createEmptyKandidatWithSingleVoteByWahlvorschlag("k21", 1)))
                        .create()))
        .toModel();
  }
}
