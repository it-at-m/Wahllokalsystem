package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import java.time.LocalDateTime;

public record EreignisModel(String beschreibung,
                            LocalDateTime uhrzeit,
                            Ereignisart ereignisart) {
}
