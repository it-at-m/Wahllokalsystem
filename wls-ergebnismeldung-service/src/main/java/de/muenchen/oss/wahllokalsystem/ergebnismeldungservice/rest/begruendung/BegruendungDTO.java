package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.begruendung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.BezirkUndWahlIDStapelartDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BegruendungDTO(@NotNull BezirkUndWahlIDStapelartDTO bezirkUndWahlIDStapelart,
                             String grund,
                             String grund2,
                             boolean nachzaehlung,
                             boolean unstimmigkeiten) {
}
