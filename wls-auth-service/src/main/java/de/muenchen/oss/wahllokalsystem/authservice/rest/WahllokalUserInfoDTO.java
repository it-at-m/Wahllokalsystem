package de.muenchen.oss.wahllokalsystem.authservice.rest;

import java.time.LocalDate;

public record WahllokalUserInfoDTO(
        String wahlbezirknummer,
        LocalDate wahltag,
        String wahlbezirkID,
        WahlbezirksartDTO wahlbezirksart,
        String wbid_wahlnummer
) {
}
