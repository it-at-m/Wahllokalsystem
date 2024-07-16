package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.ungueltigewahlscheine;

import jakarta.validation.constraints.NotNull;

public record UngueltigeWahlscheineWriteModel(@NotNull UngueltigeWahlscheineReferenceModel ungueltigeWahlscheineReferenceModel,
                                              @NotNull byte[] ungueltigeWahlscheineData) {
}
