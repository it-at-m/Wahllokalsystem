package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record WahlbezirkEreignisseDTO(@NotNull String wahlbezirkID,
                                      boolean keineVorfaelle,
                                      boolean keineVorkommnisse,
                                      List<EreignisDTO> ereigniseintraege) {
}
