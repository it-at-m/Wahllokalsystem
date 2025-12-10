package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.StapelartModel;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ErgebnisseReferenceModel(
    @NotNull String wahlbezirkID, @NotNull String wahlID, @NotNull StapelartModel stapelart) {}
