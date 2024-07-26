package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.referendumvorlagen;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record ReferendumvorlagenDTO(@NotNull String stimmzettelgebietID,
                                    @NotNull Set<ReferendumvorlageDTO> referendumvorlagen) {
}
