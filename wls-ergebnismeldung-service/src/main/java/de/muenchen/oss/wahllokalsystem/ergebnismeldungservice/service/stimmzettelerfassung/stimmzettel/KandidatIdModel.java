package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import jakarta.validation.constraints.NotNull;

public record KandidatIdModel(@NotNull String kandidatID, int nennungsNummer) {}
