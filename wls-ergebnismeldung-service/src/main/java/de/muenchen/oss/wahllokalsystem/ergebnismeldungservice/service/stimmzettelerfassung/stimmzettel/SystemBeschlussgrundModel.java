package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import jakarta.validation.constraints.NotNull;

public record SystemBeschlussgrundModel(@NotNull SystemBeschlussgrundEnumModel reason) {}
