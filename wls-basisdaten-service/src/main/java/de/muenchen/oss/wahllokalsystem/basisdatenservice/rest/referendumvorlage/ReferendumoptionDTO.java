package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.referendumvorlage;

import jakarta.validation.constraints.NotNull;

public record ReferendumoptionDTO(@NotNull String id,
                                  @NotNull String name,
                                  Long position) {
}
