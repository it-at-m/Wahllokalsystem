package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung;

import jakarta.validation.constraints.NotNull;

public record StimmzettelKandidatDTO(
    @NotNull String kandidatId, boolean isDiscarded, int votesByVoter) {}
