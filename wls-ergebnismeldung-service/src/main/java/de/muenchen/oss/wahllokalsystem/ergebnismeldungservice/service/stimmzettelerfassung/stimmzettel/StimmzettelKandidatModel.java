package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import jakarta.validation.constraints.NotNull;

public record StimmzettelKandidatModel(
    @NotNull String kandidatId, boolean isDiscarded, int votesByVoter) {}
