package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import java.time.LocalDateTime;

public record EreignisDTO(String beschreibung,
                          LocalDateTime uhrzeit,
                          EreignisartDTO ereignisart) {
}
