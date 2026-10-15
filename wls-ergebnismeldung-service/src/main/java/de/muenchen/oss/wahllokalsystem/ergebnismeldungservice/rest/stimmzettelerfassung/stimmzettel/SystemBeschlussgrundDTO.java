package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel;

import jakarta.validation.constraints.NotNull;

public record SystemBeschlussgrundDTO(@NotNull SystemBeschlussgrundEnumDTO reason) {}
