package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.model;

import jakarta.validation.constraints.NotNull;

public record Kandidat(@NotNull String identifikator,
                       @NotNull String name,
                       long listenposition,
                       boolean direktkandidat,
                       long tabellenSpalteInNiederschrift,
                       boolean einzelbewerber) {
}
