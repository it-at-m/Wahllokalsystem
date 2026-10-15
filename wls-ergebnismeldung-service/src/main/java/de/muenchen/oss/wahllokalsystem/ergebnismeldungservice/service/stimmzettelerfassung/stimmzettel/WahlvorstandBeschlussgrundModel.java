package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import jakarta.validation.constraints.NotNull;

public record WahlvorstandBeschlussgrundModel(@NotNull String text) {}
