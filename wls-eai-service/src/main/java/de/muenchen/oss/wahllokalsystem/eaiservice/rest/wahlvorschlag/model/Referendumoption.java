package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.model;

import jakarta.validation.constraints.NotNull;

public record Referendumoption(@NotNull String id,
                               @NotNull String name,
                               Long position) {
}
