package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlvorschlag;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Builder;

@Builder
public record WahlvorschlaegeDTO(@NotNull String wahlID,
                                 @NotNull String wahlbezirkID,
                                 @NotNull String stimmzettelgebietID,
                                 @NotNull Set<WahlvorschlagDTO> wahlvorschlaege) {
}
