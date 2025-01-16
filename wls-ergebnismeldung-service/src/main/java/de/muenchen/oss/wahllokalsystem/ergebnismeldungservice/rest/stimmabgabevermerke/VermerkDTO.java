package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record VermerkDTO(@NotNull long blattnummer,
                         @NotNull Set<StimmzettelDTO> stimmzetteln) {
}
