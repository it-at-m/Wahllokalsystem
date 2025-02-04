package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.StapelartModel;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BegruendungModel(@NotNull String wahlbezirkID, @NotNull String wahlID, @NotNull StapelartModel stapelart, @NotNull String grund1,
                               @NotNull String grund2,
                               @NotNull boolean nachzaehlung, @NotNull boolean unstimmigkeiten) {
}
