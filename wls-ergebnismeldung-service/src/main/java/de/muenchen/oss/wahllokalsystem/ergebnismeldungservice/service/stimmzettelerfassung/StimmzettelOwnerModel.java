package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung;

import jakarta.validation.constraints.NotNull;

public record StimmzettelOwnerModel(
    @NotNull String wahlbezirkID, @NotNull String wahlID, @NotNull String teamID) {}
