package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch;

import jakarta.validation.constraints.NotNull;

public record HandbuchReferenceModel(@NotNull String wahltagID,
                                     @NotNull WahlbezirkArtModel wahlbezirksart) {
}
