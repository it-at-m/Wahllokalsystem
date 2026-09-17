package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils;

import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.types;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.Kandidat;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.KandidatId;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.Stimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelGueltigkeit;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelID;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.Wahlvorschlag;
import java.util.UUID;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.OnCompleteCallback;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;

public class InstancioModels {

  private static final OnCompleteCallback<Stimmzettel> LINK_PARENT_STIMMZETTEL_ON_WAHLVORSCHLAEGEN =
      (Stimmzettel stimmzettel) ->
          stimmzettel
              .getWahlvorschlaege()
              .forEach(wahlvorschlag -> wahlvorschlag.setStimmzettel(stimmzettel));

  private static final OnCompleteCallback<Wahlvorschlag> LINK_PARENT_WAHLVORSCHLAG_ON_KANDIDATEN =
      (Wahlvorschlag wahlvorschlag) ->
          wahlvorschlag
              .getKandidaten()
              .forEach(kandidat -> kandidat.setWahlvorschlag(wahlvorschlag));

  public static final Settings COMMON_ENTITY_SETTINGS =
      Settings.create().set(Keys.SET_BACK_REFERENCES, true);

  public static Model<Stimmzettel> createDSESTimmzettelModel(
      final String wahlID,
      final String wahlbezirkID,
      final String teamID,
      final int stimmzettelKennung) {
    return Instancio.of(Stimmzettel.class)
        .withSettings(COMMON_ENTITY_SETTINGS)
        .ignore(types().of(UUID.class))
        .set(
            field(Stimmzettel::getId),
            new StimmzettelID(wahlbezirkID, wahlID, teamID, stimmzettelKennung))
        .toModel();
  }

  public static Model<Stimmzettel> createBlankValidStimmzettelModel(
      String wahlID, String wahlbezirkID, String teamID, int stimmzettelkennung) {
    return Instancio.ofBlank(Stimmzettel.class)
        .set(
            field(Stimmzettel::getId),
            new StimmzettelID(wahlbezirkID, wahlID, teamID, stimmzettelkennung))
        .set(field(Stimmzettel::getGueltigkeit), StimmzettelGueltigkeit.VALID)
        .onComplete(all(Stimmzettel.class), LINK_PARENT_STIMMZETTEL_ON_WAHLVORSCHLAEGEN)
        .toModel();
  }

  public static Model<Stimmzettel> createBlankInvalidStimmzettelModel(
      String wahlID, String wahlbezirkID, String teamID, int stimmzettelkennung) {
    return Instancio.ofBlank(Stimmzettel.class)
        .set(
            field(Stimmzettel::getId),
            new StimmzettelID(wahlbezirkID, wahlID, teamID, stimmzettelkennung))
        .set(field(Stimmzettel::getGueltigkeit), StimmzettelGueltigkeit.INVALID)
        .onComplete(all(Stimmzettel.class), LINK_PARENT_STIMMZETTEL_ON_WAHLVORSCHLAEGEN)
        .toModel();
  }

  public static Model<Wahlvorschlag> createBlankNonSelectedWahlvorschlagModel(
      String wahlvorschlagID) {
    return Instancio.ofBlank(Wahlvorschlag.class)
        .set(field(Wahlvorschlag::getWahlvorschlagID), wahlvorschlagID)
        .set(field(Wahlvorschlag::isSelected), false)
        .onComplete(all(Wahlvorschlag.class), LINK_PARENT_WAHLVORSCHLAG_ON_KANDIDATEN)
        .toModel();
  }

  public static Model<Wahlvorschlag> createBlankSelectedWahlvorschlagModel(String wahlvorschlagID) {
    return Instancio.ofBlank(Wahlvorschlag.class)
        .set(field(Wahlvorschlag::getWahlvorschlagID), wahlvorschlagID)
        .set(field(Wahlvorschlag::isSelected), true)
        .onComplete(all(Wahlvorschlag.class), LINK_PARENT_WAHLVORSCHLAG_ON_KANDIDATEN)
        .toModel();
  }

  public static Kandidat createBlankKandidatWithSingleVoteByWahlvorschlag(
      final String kandidatID, final int nennung) {
    return Instancio.of(createBlankKandidatModel(kandidatID, nennung))
        .set(field(Kandidat::getVotesByWahlvorschlag), 1)
        .create();
  }

  public static Kandidat createBlankKandidatWithSingleVoteByVoter(
      final String kandidatID, final int nennung) {
    return Instancio.of(createBlankKandidatModel(kandidatID, nennung))
        .set(field(Kandidat::getVotesByVoter), 1)
        .create();
  }

  public static Kandidat createBlankDiscardedKandidat(final String kandidatID, final int nennung) {
    return Instancio.of(createBlankKandidatModel(kandidatID, nennung))
        .set(field(Kandidat::isDiscarded), true)
        .create();
  }

  public static Model<Kandidat> createBlankKandidatModel(String kandidatID, int nennung) {
    return Instancio.ofBlank(Kandidat.class)
        .set(field(Kandidat::getKandidatID), new KandidatId(kandidatID, nennung))
        .set(field(Kandidat::isDiscarded), false)
        .toModel();
  }
}
