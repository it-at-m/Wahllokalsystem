package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.status;

import jakarta.validation.constraints.NotNull;

public record StimmzettelerfassungStatusDTO(@NotNull ErfassungStatusDTO status) {}
