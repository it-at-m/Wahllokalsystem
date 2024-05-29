package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.model;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record Wahlvorschlag(@NotNull String identifikator,
                            long ordnungszahl,
                            @NotNull String kurzname,
                            boolean erhaeltStimmen,
                            Set<Kandidat> kandidaten) {
}
