package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.rest.wahlvorstand;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record WahlvorstandDTO(@NotNull String wahlbezirkID,
                              LocalDateTime anwesenheitBeginn,
                              @NotNull @Size(min = 1) List<WahlvorstandsmitgliedDTO> wahlvorstandsmitglieder) {
}
