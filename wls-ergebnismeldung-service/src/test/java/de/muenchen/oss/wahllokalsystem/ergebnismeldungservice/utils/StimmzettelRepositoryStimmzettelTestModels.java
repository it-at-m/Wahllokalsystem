package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankInvalidStimmzettelModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankKandidatWithSingleVoteByVoter;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankKandidatWithSingleVoteByWahlvorschlag;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankValidStimmzettelModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankNonSelectedWahlvorschlagModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagModelWithEinzelstimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagModelWithMultipleStreichungen;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagModelWithOnlyReststimmenKandidaten;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagModelWithReststimmeAndEinzelstimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagModelWithSingleStreichungen;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagModelWithStreichungAndEinzelStimme;
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
            createBlankInvalidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .setModel(
            field(Stimmzettel::getWahlvorschlaege),
                singleWahlvorschlagModelWithOnlyReststimmenKandidaten)
        .toModel();
  }

  public static Model<Stimmzettel>
      createInvalidStimmzettelModelWithSingleWahlvorschlagWithEinzelstimme(
          final String wahlID,
          final String wahlbezirkID,
          final String teamID,
          final int stimzettelkennung) {
    return Instancio.of(
            createBlankInvalidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .setModel(field(Stimmzettel::getWahlvorschlaege), singleWahlvorschlagModelWithEinzelstimme)
        .toModel();
  }

  public static Model<Stimmzettel>
      createValidStimmzettelModelWithSingleWahlvorschlagWithEinzelstimme(
          final String wahlID,
          final String wahlbezirkID,
          final String teamID,
          final int stimzettelkennung) {
    return Instancio.of(
            createBlankValidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .setModel(field(Stimmzettel::getWahlvorschlaege), singleWahlvorschlagModelWithEinzelstimme)
        .toModel();
  }

  public static Model<Stimmzettel>
      createInvalidStimmzettelModelWithSingleWahlvorschlagWithMultipleStreichungen(
          final String wahlID,
          final String wahlbezirkID,
          final String teamID,
          final int stimzettelkennung) {
    return Instancio.of(
            createBlankInvalidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .setModel(
            field(Stimmzettel::getWahlvorschlaege),
                singleWahlvorschlagModelWithMultipleStreichungen)
        .toModel();
  }

  public static Model<Stimmzettel>
      createValidStimmzettelModelWithSingleWahlvorschlagWithMultipleStreichungen(
          final String wahlID,
          final String wahlbezirkID,
          final String teamID,
          final int stimzettelkennung) {
    return Instancio.of(
            createBlankValidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .setModel(
            field(Stimmzettel::getWahlvorschlaege),
                singleWahlvorschlagModelWithMultipleStreichungen)
        .toModel();
  }

  public static Model<Stimmzettel>
      createValidStimmzettelModelWithSingleWahlvorschlagWithSingleStreichung(
          final String wahlID,
          final String wahlbezirkID,
          final String teamID,
          final int stimzettelkennung) {
    return Instancio.of(
            createBlankValidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .setModel(
            field(Stimmzettel::getWahlvorschlaege), singleWahlvorschlagModelWithSingleStreichungen)
        .toModel();
  }

  public static Model<Stimmzettel>
      createInvalidStimmzettelModelWithSingleWahlvorschlagWithStreichungAndEinzelstimme(
          final String wahlID,
          final String wahlbezirkID,
          final String teamID,
          final int stimzettelkennung) {
    return Instancio.of(
            createBlankInvalidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .setModel(
            field(Stimmzettel::getWahlvorschlaege),
                singleWahlvorschlagModelWithStreichungAndEinzelStimme)
        .toModel();
  }

  public static Model<Stimmzettel>
      createValidStimmzettelModelWithSingleWahlvorschlagWithStreichungAndEinzelstimme(
          final String wahlID,
          final String wahlbezirkID,
          final String teamID,
          final int stimzettelkennung) {
    return Instancio.of(
            createBlankValidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .setModel(
            field(Stimmzettel::getWahlvorschlaege),
                singleWahlvorschlagModelWithStreichungAndEinzelStimme)
        .toModel();
  }

  public static Model<Stimmzettel>
      createValidStimmzettelModelWithSingleWahlvorschlagWithReststimmeAndEinzelstimme(
          final String wahlID,
          final String wahlbezirkID,
          final String teamID,
          final int stimzettelkennung) {
    return Instancio.of(
            createBlankValidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .setModel(
            field(Stimmzettel::getWahlvorschlaege),
                singleWahlvorschlagModelWithReststimmeAndEinzelstimme)
        .toModel();
  }

  public static Model<Stimmzettel>
      createInvalidStimmzettelModelWith2WahlvorschlaegenEachWithEinzelstimme(
          final String wahlID,
          final String wahlbezirkID,
          final String teamID,
          final int stimzettelkennung) {
    return Instancio.of(
            createBlankInvalidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .supply(
            field(Stimmzettel::getWahlvorschlaege),
            () ->
                List.of(
                    Instancio.of(createBlankNonSelectedWahlvorschlagModel("wv1"))
                        .supply(
                            field(Wahlvorschlag::getKandidaten),
                            () -> List.of(createBlankKandidatWithSingleVoteByVoter("k11", 1)))
                        .create(),
                    Instancio.of(createBlankNonSelectedWahlvorschlagModel("wv2"))
                        .supply(
                            field(Wahlvorschlag::getKandidaten),
                            () -> List.of(createBlankKandidatWithSingleVoteByVoter("k21", 1)))
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
            createBlankValidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .supply(
            field(Stimmzettel::getWahlvorschlaege),
            () ->
                List.of(
                    Instancio.of(createBlankNonSelectedWahlvorschlagModel("wv1"))
                        .supply(
                            field(Wahlvorschlag::getKandidaten),
                            () -> List.of(createBlankKandidatWithSingleVoteByVoter("k11", 1)))
                        .create(),
                    Instancio.of(createBlankNonSelectedWahlvorschlagModel("wv2"))
                        .supply(
                            field(Wahlvorschlag::getKandidaten),
                            () -> List.of(createBlankKandidatWithSingleVoteByVoter("k21", 1)))
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
            createBlankInvalidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .supply(
            field(Stimmzettel::getWahlvorschlaege),
            () ->
                List.of(
                    Instancio.of(createBlankNonSelectedWahlvorschlagModel("wv1"))
                        .supply(
                            field(Wahlvorschlag::getKandidaten),
                            () ->
                                List.of(createBlankKandidatWithSingleVoteByWahlvorschlag("k11", 1)))
                        .create(),
                    Instancio.of(createBlankNonSelectedWahlvorschlagModel("wv2"))
                        .supply(
                            field(Wahlvorschlag::getKandidaten),
                            () ->
                                List.of(createBlankKandidatWithSingleVoteByWahlvorschlag("k21", 1)))
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
            createBlankValidStimmzettelModel(wahlID, wahlbezirkID, teamID, stimzettelkennung))
        .supply(
            field(Stimmzettel::getWahlvorschlaege),
            () ->
                List.of(
                    Instancio.of(createBlankNonSelectedWahlvorschlagModel("wv1"))
                        .supply(
                            field(Wahlvorschlag::getKandidaten),
                            () ->
                                List.of(createBlankKandidatWithSingleVoteByWahlvorschlag("k11", 1)))
                        .create(),
                    Instancio.of(createBlankNonSelectedWahlvorschlagModel("wv2"))
                        .supply(
                            field(Wahlvorschlag::getKandidaten),
                            () ->
                                List.of(createBlankKandidatWithSingleVoteByWahlvorschlag("k21", 1)))
                        .create()))
        .toModel();
  }
}
