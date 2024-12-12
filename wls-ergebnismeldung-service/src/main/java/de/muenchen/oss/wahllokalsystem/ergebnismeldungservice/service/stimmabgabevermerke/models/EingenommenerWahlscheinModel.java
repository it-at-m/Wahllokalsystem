package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.models;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmzettelart;
import jakarta.validation.constraints.NotNull;

public record EingenommenerWahlscheinModel(
        @NotNull long anzahl,
        @NotNull Stimmzettelart stimmzettelart
) {
}
