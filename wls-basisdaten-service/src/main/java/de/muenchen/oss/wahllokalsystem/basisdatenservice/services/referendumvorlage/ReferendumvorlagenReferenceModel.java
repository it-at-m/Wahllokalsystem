package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage;

import jakarta.validation.constraints.NotNull;

public record ReferendumvorlagenReferenceModel(@NotNull String wahlID,
                                               @NotNull String wahlbezirkID) {
}
