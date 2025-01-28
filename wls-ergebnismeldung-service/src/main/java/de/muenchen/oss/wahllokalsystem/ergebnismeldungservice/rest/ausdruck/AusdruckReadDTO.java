package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record AusdruckReadDTO(@NotNull String wahlbezirkID,
                              @NotNull String wahlID,
                              @NotNull MeldungsartDTO meldungsart,
                              String content,
                              @NotNull Instant erstelltAm) {
}
