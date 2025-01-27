package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnisse;

import jakarta.validation.constraints.NotNull;

public record BezirkUndWahlIDStapelartDTO(@NotNull String wahlbezirkID, @NotNull String wahlID, @NotNull StapelartDTO stapelartDTO) {
}
