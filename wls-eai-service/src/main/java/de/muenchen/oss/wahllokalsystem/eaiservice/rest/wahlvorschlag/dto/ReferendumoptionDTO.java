package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto;

import jakarta.validation.constraints.NotNull;

public record ReferendumoptionDTO(@NotNull String id,
                                  @NotNull String name,
                                  Long position) {
}
