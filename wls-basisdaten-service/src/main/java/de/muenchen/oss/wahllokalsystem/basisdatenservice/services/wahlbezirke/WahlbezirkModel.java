package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahlbezirkArtModel;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record WahlbezirkModel(@NotNull String identifikator,
                              @NotNull WahlbezirkArtModel wahlbezirkArt,
                              @NotNull String nummer,
                              @NotNull LocalDate wahltag,
                              @NotNull String wahlnummer,
                              @NotNull String wahlID) {
}
