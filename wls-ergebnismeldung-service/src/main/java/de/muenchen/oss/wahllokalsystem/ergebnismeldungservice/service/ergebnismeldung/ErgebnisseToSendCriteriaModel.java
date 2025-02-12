package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import jakarta.validation.constraints.NotNull;

public record ErgebnisseToSendCriteriaModel(
        @NotNull String wahlID,
        @NotNull String wahlbezirkID,
        @NotNull Long waehlerverzeichnisNummer,
        @NotNull MeldungsartModel meldungsart,
        @NotNull String hauptwahlbezirkID
) {
}
