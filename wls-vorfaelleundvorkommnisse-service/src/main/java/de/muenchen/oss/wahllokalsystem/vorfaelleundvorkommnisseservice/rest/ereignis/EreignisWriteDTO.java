package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;

public record EreignisWriteDTO(String beschreibung,
                               java.time.LocalDateTime uhrzeit,
                               Ereignisart ereignisart) {
}
