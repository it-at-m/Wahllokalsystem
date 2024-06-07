package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record WahlvorschlaegeListeDTO(@NotNull @Size(min = 1) Set<WahlvorschlaegeDTO> wahlvorschlaege) {
}
