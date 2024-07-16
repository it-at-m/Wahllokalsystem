package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.ungueltigewahlscheine;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahlbezirkArtModel;
import jakarta.validation.constraints.NotNull;

public record UngueltigeWahlscheineReferenceModel(@NotNull String wahltagID,
                                                  @NotNull WahlbezirkArtModel wahlbezirksart) {
}
