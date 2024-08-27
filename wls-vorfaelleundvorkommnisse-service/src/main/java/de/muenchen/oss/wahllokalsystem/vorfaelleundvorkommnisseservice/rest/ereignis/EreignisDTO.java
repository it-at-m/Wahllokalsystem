package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereigniseintrag;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record EreignisDTO(@NotNull String wahlbezirkID,
                          Boolean keineVorfaelle,
                          Boolean keineVorkommnisse,
                          java.util.List<Ereigniseintrag> ereigniseintrag) {
}
