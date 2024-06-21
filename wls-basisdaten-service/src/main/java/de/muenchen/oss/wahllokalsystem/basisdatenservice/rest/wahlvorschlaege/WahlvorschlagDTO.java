package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlvorschlaege;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record WahlvorschlagDTO(@NotNull String identifikator,
                               @NotNull Long ordnungszahl,
                               @NotNull String kurzname,
                               @NotNull Boolean erhaeltStimmen,
                               Set<KandidatDTO> kandidaten) {
}
