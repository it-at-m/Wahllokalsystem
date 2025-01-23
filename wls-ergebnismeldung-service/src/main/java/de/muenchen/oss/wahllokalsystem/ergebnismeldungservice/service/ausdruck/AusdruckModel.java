package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record AusdruckModel(@Valid @NotNull WahlUndBezirkIDUndMeldungsart wahlUndBezirkIDUndMeldungsart,
                            String content,
                            Instant erstelltAm) {
}
