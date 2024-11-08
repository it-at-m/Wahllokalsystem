package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.aoueaiClient;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlvorstandsmitgliedModel;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record WahlvorstandModel(String wahlbezirkID,
                                LocalDateTime anwesenheitBeginn,
                                List<WahlvorstandsmitgliedModel> wahlvorstandsmitglieder) {
}
