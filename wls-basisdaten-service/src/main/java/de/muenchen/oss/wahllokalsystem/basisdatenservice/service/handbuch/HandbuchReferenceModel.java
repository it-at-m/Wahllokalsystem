package de.muenchen.oss.wahllokalsystem.basisdatenservice.service.handbuch;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.common.WahlbezirkArtModel;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record HandbuchReferenceModel(
    @NotNull String wahltagID, @NotNull WahlbezirkArtModel wahlbezirksart) {}
