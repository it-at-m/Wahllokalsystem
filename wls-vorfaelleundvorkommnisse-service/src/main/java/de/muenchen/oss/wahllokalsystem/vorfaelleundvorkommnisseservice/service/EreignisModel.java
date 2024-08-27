package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereigniseintrag;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record EreignisModel(@NotNull String wahlbezirkID,
                            Boolean keineVorfaelle,
                            Boolean keineVorkommnisse,
                            java.util.List<Ereigniseintrag> ereigniseintrag) {
}
