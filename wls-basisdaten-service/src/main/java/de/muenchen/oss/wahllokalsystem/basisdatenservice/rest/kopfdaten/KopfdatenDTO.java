package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.kopfdaten;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record KopfdatenDTO(@NotNull String wahlID,
                           @NotNull String wahlbezirkID,
                           @NotNull String gemeinde,
                           @NotNull StimmzettelgebietsartDTO stimmzettelgebietsart,
                           @NotNull String stimmzettelgebietsnummer,
                           @NotNull String stimmzettelgebietsname,
                           @NotNull String wahlname,
                           @NotNull String wahlbezirknummer) {
}
