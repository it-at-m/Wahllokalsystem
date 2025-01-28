package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;

public record ErgebnisseToSendCriteriaModel(
        String wahlID,
        String wahlbezirkID,
        Long waehlerverzeichnisNummer,
        MeldungsartModel meldungsart,
        String hauptwahlbezirkID
) {
}
