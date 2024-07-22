package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage;

import jakarta.validation.constraints.NotNull;

public record ReferendumoptionModel(@NotNull String id,
                                    @NotNull String name,
                                    Long position) {
}
