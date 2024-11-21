package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand;

import jakarta.validation.constraints.NotNull;

public record WahlModel(@NotNull Long reihenfolge,
                        @NotNull WahlartModel wahlart) {
}
