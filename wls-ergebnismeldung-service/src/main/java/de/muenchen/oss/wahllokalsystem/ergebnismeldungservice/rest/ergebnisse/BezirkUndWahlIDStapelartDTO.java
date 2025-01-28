package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.StapelartDTO;
import jakarta.validation.constraints.NotNull;

public record BezirkUndWahlIDStapelartDTO(@NotNull String wahlbezirkID, @NotNull String wahlID, @NotNull StapelartDTO stapelartDTO) {
}
