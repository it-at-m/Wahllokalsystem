package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.briefwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.common.WahlurneDTO;
import jakarta.validation.constraints.NotNull;

public record BriefwahlvorbereitungDTO(@NotNull String wahlbezirkID,
                                     @NotNull java.util.List<WahlurneDTO> urnenAnzahl) {
}
