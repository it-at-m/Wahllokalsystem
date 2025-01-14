package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Meldungsart;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record AusdruckReadDTO(@NotNull String wahlbezirkID,
                              @NotNull String wahlID,
                              @NotNull Meldungsart meldungsart,
                              String content,
                              @NotNull Instant erstelltAm) {
}
