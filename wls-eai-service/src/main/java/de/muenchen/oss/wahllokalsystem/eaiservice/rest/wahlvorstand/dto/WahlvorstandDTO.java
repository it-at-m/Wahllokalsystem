package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record WahlvorstandDTO(@NotNull String wahlbezirkID,
                              @NotNull @Size(min = 1) Set<WahlvorstandsmitgliedDTO> mitglieder) {
}
