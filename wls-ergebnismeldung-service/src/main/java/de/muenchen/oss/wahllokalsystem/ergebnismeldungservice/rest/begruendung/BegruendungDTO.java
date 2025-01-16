package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.begruendung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.BezirkUndWahlIDStapelart;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BegruendungDTO(@NotNull BezirkUndWahlIDStapelart bezirkUndWahlIDStapelart,
                             String grund1,
                             String grund2,
                             boolean nachzaehlung,
                             boolean unstimmigkeiten) {
}
