package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record HandbuchWriteModel(@NotNull HandbuchReferenceModel handbuchReferenceModel,
                                 @NotNull byte[] handbuchData) {
}
