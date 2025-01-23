package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke;

import jakarta.validation.constraints.NotNull;

public record StimmzettelModel(
        @NotNull long anzahl,
        @NotNull StimmzettelartModel stimmzettelart
) {
}
