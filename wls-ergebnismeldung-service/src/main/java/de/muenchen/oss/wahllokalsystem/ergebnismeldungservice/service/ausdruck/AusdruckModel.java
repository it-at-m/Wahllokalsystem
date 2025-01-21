package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record AusdruckModel(@NotNull WahlUndBezirkIDUndMeldungsart wahlUndBezirkIDUndMeldungsart,
                            String content,
                            @NotNull Instant erstelltAm) {
}
