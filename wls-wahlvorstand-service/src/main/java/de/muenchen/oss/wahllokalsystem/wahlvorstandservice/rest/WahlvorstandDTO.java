package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.rest;

import java.time.LocalDateTime;
import java.util.List;

public record WahlvorstandDTO(String wahlbezirkID,
                              LocalDateTime anwesenheitBeginn,
                              List<WahlvorstandsmitgliedDTO> mitglieder) {}
