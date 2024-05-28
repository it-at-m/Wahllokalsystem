package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.testdaten;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.Wahlurne;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.common.WahlurneDTO;

public class WahlurneTestdatenfactory {

    public static Wahlurne.WahlurneBuilder initValid(final String wahlID) {
        return Wahlurne.builder().wahlID(wahlID).anzahl(1).urneVersiegelt(true);
    }

    public static WahlurneDTO.WahlurneDTOBuilder initValidDTO(final String wahlID) {
        return WahlurneDTO.builder().wahlID(wahlID);
    }
}
