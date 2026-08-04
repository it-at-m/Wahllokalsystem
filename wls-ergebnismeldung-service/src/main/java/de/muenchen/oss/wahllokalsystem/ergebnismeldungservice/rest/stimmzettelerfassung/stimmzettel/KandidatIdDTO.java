package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel;

import io.swagger.v3.oas.annotations.media.Schema;

public record KandidatIdDTO(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String kandidatID,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int nennungsNummer) {}
