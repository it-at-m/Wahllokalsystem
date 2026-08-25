package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel;

import jakarta.validation.constraints.NotNull;

public record WahlvorstandBeschlussgrundDTO(@NotNull String text) {}
