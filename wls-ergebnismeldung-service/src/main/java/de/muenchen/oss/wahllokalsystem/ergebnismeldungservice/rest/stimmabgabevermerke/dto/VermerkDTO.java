package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record VermerkDTO(@NotNull long blattnummer,
                         @NotNull Set<StimmzettelDTO> stimmzetteln){
}
