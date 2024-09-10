package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import java.time.LocalDateTime;

public record EreignisDTO(String beschreibung,
                          LocalDateTime uhrzeit,
                          Ereignisart ereignisart) {
}
