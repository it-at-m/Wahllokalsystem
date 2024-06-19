package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlvorschlaege;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record WLSWahlvorschlagDTO(@NotNull String identifikator,
                                  @NotNull Boolean erhaeltStimmen,
                                  @NotNull String kurzname,
                                  @NotNull Long ordnungszahl,
                                  @NotNull java.util.Set<de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.eai.model.WahlvorschlagDTO> wahlvorschlaege,
                                  Set<WLSKandidatDTO> kandidaten) {
}
