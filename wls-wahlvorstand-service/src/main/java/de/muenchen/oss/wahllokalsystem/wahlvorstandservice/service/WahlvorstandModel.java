package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.Wahlvorstandsmitglied;

import java.time.LocalDateTime;
import java.util.List;

// todo: ist das model richtig?
public record WahlvorstandModel(String wahlbezirkID,
                                LocalDateTime anwesenheitBeginn,
                                List<Wahlvorstandsmitglied> mitglieder) {
}
