package de.muenchen.oss.wahllokalsystem.briefwahlservice.rest.wahlbriefdaten;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record WahlbriefdatenDTO(@NotNull String wahlbezirkID,
                                Long wahlbriefe,
                                Long verzeichnisseUngueltige,
                                Long nachtraege,
                                Long nachtraeglichUeberbrachte,
                                LocalDateTime zeitNachtraeglichUeberbrachte) {
}
