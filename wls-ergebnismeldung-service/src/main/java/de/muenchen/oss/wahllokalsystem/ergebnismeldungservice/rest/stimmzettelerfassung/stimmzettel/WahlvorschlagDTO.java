package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record WahlvorschlagDTO(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String wahlvorschlagID,
    boolean selected,
    List<KandidatDTO> kandidaten) {}
