package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.MeldungsartDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WahlUndBezirkIDUndMeldungsartDTO(@NotBlank String wahlbezirkID,
                                               @NotBlank String wahlID,
                                               @NotNull MeldungsartDTO meldungsartModel) {

}
