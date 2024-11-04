package de.muenchen.oss.wahllokalsystem.authservice.service;

import java.time.LocalDate;

public record WahllokalUserInfoModel(
        String wahlbezirknummer,
        LocalDate wahltag,
        String wahlbezirkID,
        WahlbezirksartModel wahlbezirksart,
        String wbid_wahlnummer
) {
}
