package de.muenchen.oss.wahllokalsystem.basisdatenservice.service.referendumvorlagen;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder public record ReferendumvorlagenReferenceModel(@NotNull String wahlID,@NotNull String wahlbezirkID){}
