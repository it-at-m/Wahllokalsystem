package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service;

import java.time.LocalDateTime;
import java.util.List;

public record WahlvorstandModel(String wahlbezirkID,
                                LocalDateTime anwesenheitBeginn,
                                List<WahlvorstandsmitgliedModel> mitglieder) {
}
