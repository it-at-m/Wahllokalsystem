package de.muenchen.oss.wahllokalsystem.authservice.service;

import de.muenchen.oss.wahllokalsystem.authservice.rest.WahlbezirksartDTO;
import java.time.LocalDate;

public record WahllokalUserInfoModel(
        String wahlbezirknummer,
        LocalDate wahltag,
        String wahlbezirkID,
        WahlbezirksartDTO wahlbezirksart,
        String wbid_wahlnummer
) {
}
