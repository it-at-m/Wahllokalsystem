package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.MeldungsartDTO;
import jakarta.validation.constraints.NotNull;

public record SendErgebnisParameter(
        @NotNull String wahlID,
        @NotNull String wahlbezirkID,
        @NotNull Long waehlerverzeichnisNummer,
        @NotNull MeldungsartDTO meldungsart,
        @NotNull String hauptwahlbezirkID
) {
}
