package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record WaehlerStimmzettelDTO(
    @NotNull String wahlbezirkID,
    @NotNull String wahlID,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int stimmzettelNummer,
    @NotNull List<Integer> selectedWahlvorschlaegeOrdnungszahlen,
    @NotNull List<StimmzettelKandidatDTO> kandidaten) {}
