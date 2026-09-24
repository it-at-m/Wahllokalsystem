package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.DokumentartDTO;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record AusdruckReadDTO(
    @NotNull String wahlbezirkID,
    @NotNull String wahlID,
    @NotNull DokumentartDTO dokumentart,
    String content,
    @NotNull Instant erstelltAm) {}
