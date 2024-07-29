package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record ReferendumvorlageModel(@NotNull String wahlvorschlagID,
                                     @NotNull Long ordnungszahl,
                                     @NotNull String kurzname,
                                     @NotNull String frage,
                                     @NotNull Set<ReferendumoptionModel> referendumoptionen) {
}
