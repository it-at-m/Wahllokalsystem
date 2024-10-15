package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.rest.wahlvorstand;

import java.time.LocalDateTime;
import java.util.List;

public record WahlvorstandDTO(String wahlbezirkID,
                              LocalDateTime anwesenheitBeginn,
                              List<WahlvorstandsmitgliedDTO> wahlvorstandsmitglieder) {
}
