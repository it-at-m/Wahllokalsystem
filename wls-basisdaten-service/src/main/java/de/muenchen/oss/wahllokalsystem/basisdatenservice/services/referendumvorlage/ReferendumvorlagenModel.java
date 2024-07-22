package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record ReferendumvorlagenModel(@NotNull String stimmzettelgebietID,
                                      @NotNull Set<ReferendumvorlageModel> referendumvorlagen) {
}