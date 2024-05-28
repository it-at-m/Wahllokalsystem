package de.muenchen.oss.wahllokalsystem.briefwahlservice.service.wahlbriefdaten;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record WahlbriefdatenModel(@NotNull String wahlbezirkID,
                                  Long wahlbriefe,
                                  Long verzeichnisseUngueltige,
                                  Long nachtraege,
                                  Long nachtraeglichUeberbrachte,
                                  LocalDateTime zeitNachtraeglichUeberbrachte) {
}
