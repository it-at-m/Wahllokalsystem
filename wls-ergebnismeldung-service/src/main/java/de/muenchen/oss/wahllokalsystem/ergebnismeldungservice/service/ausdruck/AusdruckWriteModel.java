package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record AusdruckWriteModel(
    @Valid @NotNull WahlUndBezirkIDUndDokumentartModel wahlUndBezirkIDUndDokumentartModel,
    String content) {}
