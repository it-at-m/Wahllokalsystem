package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record AusdruckReadModel(
    @Valid @NotNull WahlUndBezirkIDUndMeldungsartModel wahlUndBezirkIDUndMeldungsartModel,
    String content,
    Instant erstelltAm) {}
