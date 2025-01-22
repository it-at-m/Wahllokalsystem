package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnisse;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record ErgebnisseDTO(@NotNull BezirkUndWahlIDStapelartDTO bezirkUndWahlIDStapelartDTO,
                            @NotNull List<ErgebnisDTO> ergebnisse) {
}
