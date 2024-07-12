package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record ReferendumvorlagenDTO(@NotNull String stimmzettelgebietID,
                                    @NotNull @Size(min = 1) Set<ReferendumvorlageDTO> referendumvorlagen) {
}
