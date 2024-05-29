package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record WahlvorschlaegeListe(@NotNull @Size(min = 1) Set<Wahlvorschlaege> wahlvorschlaege) {
}
