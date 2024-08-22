package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereigniseintraege;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record EreignisDTO(@NotNull String wahlbezirkID,
                          Boolean keineVorfaelle,
                          Boolean keineVorkommnisse,
                          java.util.List<Ereigniseintraege> ereigniseintraege) {
}
