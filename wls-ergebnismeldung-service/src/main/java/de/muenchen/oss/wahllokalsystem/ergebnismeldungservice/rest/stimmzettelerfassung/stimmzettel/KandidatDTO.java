package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel;

import io.swagger.v3.oas.annotations.media.Schema;

public record KandidatDTO(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) KandidatIdDTO id,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Boolean discarded,
    Integer votesByVoter,
    Integer invalidVotes,
    Integer votesByWahlvorschlag) {}
