package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record Wahlvorschlaege(@NotNull String wahlbezirkID,
                              @NotNull String wahlID,
                              @NotNull String stimmzettelgebietID,
                              @NotNull @Size(min = 1) Set<Wahlvorschlag> wahlvorschlaege) {
}
