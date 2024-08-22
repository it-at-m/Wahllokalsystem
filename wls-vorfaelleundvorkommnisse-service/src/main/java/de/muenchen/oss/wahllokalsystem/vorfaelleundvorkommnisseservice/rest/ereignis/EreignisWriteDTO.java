package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereigniseintraege;

public record EreignisWriteDTO(Boolean keineVorfaelle,
                               Boolean keineVorkommnisse,
                               java.util.List<Ereigniseintraege> ereigniseintraege) {
}
