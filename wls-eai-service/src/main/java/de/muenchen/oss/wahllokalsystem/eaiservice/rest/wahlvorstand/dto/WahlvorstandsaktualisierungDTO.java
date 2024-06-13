package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

public record WahlvorstandsaktualisierungDTO(@NotNull String wahlbezirkID,
                                             @NotNull @Size(min = 1) Set<WahlvorstandsmitgliedAktualisierungDTO> mitglieder,
                                             @NotNull LocalDateTime anwesenheitBeginn) {
}
