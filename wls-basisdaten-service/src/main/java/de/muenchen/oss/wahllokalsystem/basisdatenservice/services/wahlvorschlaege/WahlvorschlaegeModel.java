package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Builder;

@Builder
public record WahlvorschlaegeModel(@NotNull String wahlID, @NotNull String wahlbezirkID, @NotNull String stimmzettelgebietID, @NotNull Set<WahlvorschlagModel> wahlvorschlaege) {

}
