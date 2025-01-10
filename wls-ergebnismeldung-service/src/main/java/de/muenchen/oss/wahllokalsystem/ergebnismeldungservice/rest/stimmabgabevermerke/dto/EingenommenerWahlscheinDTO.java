package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.dto;

import jakarta.validation.constraints.NotNull;

public record EingenommenerWahlscheinDTO(@NotNull long anzahl,
                                         @NotNull StimmzettelartDTO stimmzettelart){
}
