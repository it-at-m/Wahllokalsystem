package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ReferendumvorlagenReferenceModel(@NotNull String wahlID,
                                               @NotNull String wahlbezirkID) {
}
