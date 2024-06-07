package de.muenchen.oss.wahllokalsystem.briefwahlservice.rest.wahlbriefdaten;

import java.time.LocalDateTime;

public record WahlbriefdatenWriteDTO(Long wahlbriefe,
                                     Long verzeichnisseUngueltige,
                                     Long nachtraege,
                                     Long nachtraeglichUeberbrachte,
                                     LocalDateTime zeitNachtraeglichUeberbrachte) {
}
