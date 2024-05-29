package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.model;

import jakarta.validation.constraints.NotNull;

public record Wahlberechtigte(@NotNull String wahlID,
                              @NotNull String wahlbezirkID,
                              long a1,
                              long a2,
                              long a3) {
}
