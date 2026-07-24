package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record StimmzettelKandidatDTO(
    @NotNull String kandidatId,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean isDiscarded,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int votesByVoter) {}
