package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnis;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record ErgebnisseModel(@NotNull String wahlbezirkID, @NotNull String wahlID, @NotNull Stapelart stapelart, @NotNull List<Ergebnis> ergebnisse) {
}
