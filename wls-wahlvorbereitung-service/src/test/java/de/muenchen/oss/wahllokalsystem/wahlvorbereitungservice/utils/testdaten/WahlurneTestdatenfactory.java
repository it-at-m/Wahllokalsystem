package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.testdaten;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.Wahlurne;

public class WahlurneTestdatenfactory {

    public static Wahlurne.WahlurneBuilder initValid(final String wahlID) {
        return Wahlurne.builder().wahlID(wahlID).anzahl(1).urneVersiegelt(true);
    }
}
