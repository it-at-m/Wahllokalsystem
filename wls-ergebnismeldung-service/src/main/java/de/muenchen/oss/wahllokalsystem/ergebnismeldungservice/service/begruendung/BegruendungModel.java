package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BegruendungModel(@NotNull String wahlbezirkID, @NotNull String wahlID, @NotNull Stapelart stapelart, @NotNull String grund1,
                               @NotNull String grund2,
                               @NotNull boolean nachzaehlung, @NotNull boolean unstimmigkeiten) {
}
