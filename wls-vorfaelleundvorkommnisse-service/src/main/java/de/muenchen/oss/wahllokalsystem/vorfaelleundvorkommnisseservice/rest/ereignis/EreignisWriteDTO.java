package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereigniseintrag;

public record EreignisWriteDTO(Boolean keineVorfaelle,
                               Boolean keineVorkommnisse,
                               java.util.List<Ereigniseintrag> ereigniseintrag) {
}
