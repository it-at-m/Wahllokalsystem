package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke;

import jakarta.validation.constraints.NotNull;

public record StimmzettelDTO(@NotNull long anzahl,
                             @NotNull StimmzettelartDTO stimmzettelart) {
}
