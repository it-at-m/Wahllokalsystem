package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WahlUndBezirkIDUndDokumentartModel(
    @NotBlank String wahlbezirkID,
    @NotBlank String wahlID,
    @NotNull DokumentartModel dokumentart) {}
