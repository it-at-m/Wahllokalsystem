package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.rest.wahlvorstand;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record WahlvorstandWriteDTO(LocalDateTime anwesenheitBeginn,
                                   List<WahlvorstandsmitgliedDTO> wahlvorstandsmitglieder) {
}
