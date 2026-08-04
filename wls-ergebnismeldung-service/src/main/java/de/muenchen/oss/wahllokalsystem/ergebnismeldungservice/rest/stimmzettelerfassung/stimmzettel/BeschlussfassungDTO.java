package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel;

import io.swagger.v3.oas.annotations.media.Schema;

public record BeschlussfassungDTO(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int pro,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int contra,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String text) {}
