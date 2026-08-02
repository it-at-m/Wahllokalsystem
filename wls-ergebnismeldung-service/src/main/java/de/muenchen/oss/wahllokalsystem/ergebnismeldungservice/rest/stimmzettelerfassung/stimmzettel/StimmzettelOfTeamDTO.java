package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record StimmzettelOfTeamDTO(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int stimmzettelkennung,
    List<Integer> selectedWahlvorschlaegeOrdnungszahlen,
    List<StimmzettelKandidatDTO> kandidaten) {}
