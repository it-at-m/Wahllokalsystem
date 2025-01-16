package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.BezirkUndWahlIDStapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnis;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record ErgebnisseDTO(@NotNull BezirkUndWahlIDStapelart bezirkUndWahlIDStapelart,
                            @NotNull List<Ergebnis> ergebnisse) {
}
