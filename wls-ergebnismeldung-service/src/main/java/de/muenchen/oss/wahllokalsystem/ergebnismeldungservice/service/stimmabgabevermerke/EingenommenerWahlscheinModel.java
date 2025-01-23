package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke;

import jakarta.validation.constraints.NotNull;

public record EingenommenerWahlscheinModel(
        @NotNull long anzahl,
        @NotNull StimmzettelartModel stimmzettelart
) {
}
