package de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer;

import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahlbezirkArtModel;
import java.time.LocalDate;
import java.util.List;

public record WahllokalBenutzerModel(
        String wahlbezirkID,
        String wahlbezirknummer,
        LocalDate wahltag,
        WahlbezirkArtModel wahlbezirksart,
        List<TripleOfWahlbezirkIDWahlnummerWahlIDModel> wbid_wahlnummer
) {
}
