package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.ungueltigewahlscheine;

import jakarta.validation.constraints.NotNull;

public record UngueltigeWahlscheineWriteModel(@NotNull UngueltigeWahlscheineReference ungueltigeWahlscheineReference,
                                              @NotNull byte[] ungueltigeWahlscheineData) {
}
