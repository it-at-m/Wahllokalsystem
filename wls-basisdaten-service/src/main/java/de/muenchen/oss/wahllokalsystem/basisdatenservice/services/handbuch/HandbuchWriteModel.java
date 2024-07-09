package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch;

import jakarta.validation.constraints.NotNull;

public record HandbuchWriteModel(@NotNull HandbuchReferenceModel handbuchReferenceModel,
                                 @NotNull byte[] handbuchData) {
}
