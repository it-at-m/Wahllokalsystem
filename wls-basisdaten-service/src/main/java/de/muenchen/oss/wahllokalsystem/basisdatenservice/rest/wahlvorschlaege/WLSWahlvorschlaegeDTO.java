package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlvorschlaege;

import jakarta.validation.constraints.NotNull;

public record WLSWahlvorschlaegeDTO(@NotNull String wahlID, @NotNull String wahlbezirkID, @NotNull String stimmzettelgebietID, @NotNull java.util.Set<WLSWahlvorschlagDTO> wahlvorschlaege) {
}
