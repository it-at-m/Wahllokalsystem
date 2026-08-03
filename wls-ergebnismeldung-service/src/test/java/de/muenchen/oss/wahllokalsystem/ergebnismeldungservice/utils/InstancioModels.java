package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils;

import static org.instancio.Select.field;
import static org.instancio.Select.types;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.DSEStimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelID;
import java.util.UUID;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;

public class InstancioModels {

  private static final Settings COMMON_ENTITY_SETTINGS =
      Settings.create().set(Keys.SET_BACK_REFERENCES, true);

  public static Model<DSEStimmzettel> createDSESTimmzettelModel(
      final String wahlID,
      final String wahlbezirkID,
      final String teamID,
      final int stimmzettelKennung) {
    return Instancio.of(DSEStimmzettel.class)
        .withSettings(COMMON_ENTITY_SETTINGS)
        .ignore(types().of(UUID.class))
        .set(
            field(DSEStimmzettel::getId),
            new StimmzettelID(wahlbezirkID, wahlID, teamID, stimmzettelKennung))
        .toModel();
  }
}
