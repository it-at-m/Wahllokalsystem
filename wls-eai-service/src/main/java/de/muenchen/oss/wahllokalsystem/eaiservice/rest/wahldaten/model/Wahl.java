package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.model;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.model.Wahlart;
import java.time.LocalDate;

public record Wahl(String identifikator,
                   String name,
                   Wahlart wahlart,
                   LocalDate wahltag,
                   String nummer) {
}
