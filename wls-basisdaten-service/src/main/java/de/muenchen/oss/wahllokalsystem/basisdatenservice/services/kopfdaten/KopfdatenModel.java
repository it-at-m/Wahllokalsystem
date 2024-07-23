package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.Stimmzettelgebietsart;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.validation.constraints.NotNull;

public record KopfdatenModel(@NotNull BezirkUndWahlID bezirkUndWahlID,
                             @NotNull String gemeinde,
                             @NotNull Stimmzettelgebietsart stimmzettelgebietsart,
                             @NotNull String stimmzettelgebietsnummer,
                             @NotNull String stimmzettelgebietsname,
                             @NotNull String wahlname,
                             @NotNull String wahlbezirknummer) {
}
