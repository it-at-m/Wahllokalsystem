package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.referendumvorlage;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record ReferendumvorlageDTO(@NotNull String wahlvorschlagID,
                                   @NotNull Long ordnungszahl,
                                   @NotNull String kurzname,
                                   @NotNull String frage,
                                   @NotNull Set<ReferendumoptionDTO> referendumoptionen) {
}
