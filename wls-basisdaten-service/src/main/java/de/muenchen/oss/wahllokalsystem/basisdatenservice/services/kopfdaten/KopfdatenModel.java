package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.StimmzettelgebietsartModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record KopfdatenModel(@NotNull BezirkUndWahlID bezirkUndWahlID,
                             @NotNull String gemeinde,
                             @NotNull StimmzettelgebietsartModel stimmzettelgebietsart,
                             @NotNull String stimmzettelgebietsnummer,
                             @NotNull String stimmzettelgebietsname,
                             @NotNull String wahlname,
                             @NotNull String wahlbezirknummer) {
}
