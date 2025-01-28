package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.MeldungsartDTO;

public record SendErgebnisParameter(
        String wahlID,
        String wahlbezirkID,
        Long waehlerverzeichnisNummer,
        MeldungsartDTO meldungsart,
        String hauptwahlbezirkID
) {
}
