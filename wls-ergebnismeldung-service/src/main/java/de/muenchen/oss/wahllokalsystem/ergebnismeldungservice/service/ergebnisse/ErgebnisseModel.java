package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record ErgebnisseModel(@NotNull String wahlbezirkID, @NotNull String wahlID, @NotNull StapelartModel stapelart,
                              @NotNull List<ErgebnisModel> ergebnisse) {
}
