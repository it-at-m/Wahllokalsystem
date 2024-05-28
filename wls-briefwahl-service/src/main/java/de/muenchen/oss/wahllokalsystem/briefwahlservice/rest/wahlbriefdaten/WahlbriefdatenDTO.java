package de.muenchen.oss.wahllokalsystem.briefwahlservice.rest.wahlbriefdaten;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record WahlbriefdatenDTO(@NotNull String wahlbezirkID,
                                Long wahlbriefe,
                                Long verzeichnisseUngueltige,
                                Long nachtraege,
                                Long nachtraeglichUeberbrachte,
                                LocalDateTime zeitNachtraeglichUeberbrachte) {
}
