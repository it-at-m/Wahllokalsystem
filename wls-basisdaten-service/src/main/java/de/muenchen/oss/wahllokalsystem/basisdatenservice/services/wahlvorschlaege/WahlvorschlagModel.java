package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Builder;

@Builder
public record WahlvorschlagModel(
        @NotNull String identifikator,
        @NotNull Long ordnungszahl,
        @NotNull String kurzname,
        @NotNull Boolean erhaeltStimmen,
        Set<KandidatModel> kandidaten
) {

}
