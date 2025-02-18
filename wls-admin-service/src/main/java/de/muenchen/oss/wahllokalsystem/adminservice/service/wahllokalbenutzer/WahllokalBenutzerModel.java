package de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer;

import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahlbezirkArtModel;
import java.time.LocalDate;
import java.util.ArrayList;

public record WahllokalBenutzerModel(
        String wahlbezirkID,
        String wahlbezirknummer,
        LocalDate wahltag,
        WahlbezirkArtModel wahlbezirksart,
        ArrayList<TripleOfWahlbezirkIDWahlNummerWahlIDModel> wbid_wahlnummer
) {
}
